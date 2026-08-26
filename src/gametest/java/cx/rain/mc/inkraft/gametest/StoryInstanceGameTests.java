package cx.rain.mc.inkraft.gametest;

import com.bladecoder.ink.runtime.InkList;
import com.bladecoder.ink.runtime.InkListItem;
import com.bladecoder.ink.runtime.Story;
import com.bladecoder.ink.runtime.StoryState;
import com.mojang.brigadier.CommandDispatcher;
import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.api.platform.storage.IInkPlayerData;
import cx.rain.mc.inkraft.engine.EngineManager;
import cx.rain.mc.inkraft.storage.InkPlayerData;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.StoryRuntime;
import cx.rain.mc.inkraft.story.function.system.flow.IsFlowEndedFunction;
import cx.rain.mc.inkraft.story.function.system.line.SetLineTicksFunction;
import cx.rain.mc.inkraft.story.function.system.variable.GetVariableFunction;
import cx.rain.mc.inkraft.story.function.system.variable.SetVariableFunction;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.InkListStoryValue;
import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.StringStoryValue;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.world.entity.Entity;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class StoryInstanceGameTests {
    private static final Identifier TRANSACTION_BASE = testStory("transaction_base");
    private static final Identifier TRANSACTION_REPLACEMENT = testStory("transaction_replacement");
    private static final Identifier RESET_DURING_CONTINUE = testStory("reset_during_continue");
    private static final Identifier RUNTIME_FAILURE_DURING_CONTINUE =
            testStory("runtime_failure_during_continue");
    private static final Identifier SYSTEM_FUNCTION_CONTEXT = testStory("test4");
    private static final Identifier PLAYER_STAT_FUNCTIONS = testStory("player_stats");
    private static final Identifier UUID_FUNCTIONS = testStory("uuid_functions");
    private static final Identifier MISSING = testStory("missing");

    private StoryInstanceGameTests() {
    }

    public static void flowBatchIsFifoAndBeatsPause(GameTestHelper helper) {
        var context = startStory(helper, TRANSACTION_BASE);

        helper.startSequence()
                .thenWaitUntil(() -> requireRuntime(helper, context))
                .thenExecute(() -> {
                    context.instance().stop();
                    helper.assertTrue(context.instance().requestFlowTo("missing"),
                            "A missing flow request should be accepted for apply-time validation");
                    helper.assertTrue(context.instance().requestRemoveFlow(StoryState.kDefaultFlowName),
                            "A default-flow removal request should be accepted for apply-time validation");
                    helper.assertTrue(context.instance().requestFlowTo(StoryState.kDefaultFlowName),
                            "Switching to the current flow should be accepted as an apply-time no-op");

                    context.instance().requestPause();
                    helper.assertTrue(context.instance().requestNewFlow("temporary", "buy"),
                            "The temporary flow creation should be accepted");
                    helper.assertTrue(context.instance().requestRemoveFlow("temporary"),
                            "The queued temporary flow removal should be accepted");
                    helper.assertTrue(context.instance().requestNewFlow("first", "buy"),
                            "The first flow transaction should be accepted");
                    helper.assertTrue(context.instance().requestNewFlow("second", "buy"),
                            "The second flow transaction should be accepted");
                    helper.assertTrue(context.instance().requestFlowTo(StoryState.kDefaultFlowName),
                            "The final default-flow switch should be accepted");
                })
                .thenWaitUntil(() -> {
                    var runtime = requireRuntime(helper, context);
                    helper.assertFalse(runtime.hasFlow("missing"), "An invalid switch created its target flow");
                    helper.assertFalse(runtime.hasFlow("temporary"), "The queued flow removal was dropped");
                    helper.assertTrue(runtime.hasFlow("first"), "The first flow was dropped");
                    helper.assertTrue(runtime.hasFlow("second"), "The second flow was dropped");
                    helper.assertValueEqual(runtime.getFlowName(), StoryState.kDefaultFlowName, "current flow");
                    helper.assertTrue(context.data().getState() != null,
                            "A successful flow batch must save state");
                    helper.assertTrue(context.data().getContinuousToken() != null,
                            "A successful flow batch must expose manual continuation");
                    helper.assertFalse(context.instance().isStoryRunning(),
                            "A successful flow batch must stop automatic playback");
                })
                .thenExecute(context::close)
                .thenSucceed();
    }

    public static void lifecycleLastWriteWins(GameTestHelper helper) {
        var context = startStory(helper, TRANSACTION_BASE);
        var oldRuntime = new AtomicReference<StoryRuntime>();

        helper.startSequence()
                .thenWaitUntil(() -> requireRuntime(helper, context))
                .thenExecute(() -> {
                    context.instance().stop();
                    oldRuntime.set(requireRuntime(helper, context));
                    context.data().setVariable("kept", new IntStoryValue(7));

                    helper.assertTrue(context.instance().requestNewFlow("discarded", "buy"),
                            "The flow transaction should initially be accepted");
                    context.instance().requestPause();
                    context.instance().requestResetStory();
                    helper.assertTrue(context.instance().requestNewStory(TRANSACTION_REPLACEMENT),
                            "The replacement candidate should be accepted");

                    helper.assertTrue(context.instance().getRuntime().orElse(null) == oldRuntime.get(),
                            "A replacement must not publish inside the requesting tick");
                    helper.assertValueEqual(context.data().getStory(), TRANSACTION_BASE,
                            "story id before replacement commit");
                })
                .thenWaitUntil(() -> {
                    var runtime = requireRuntime(helper, context);
                    helper.assertTrue(runtime != oldRuntime.get(), "The replacement did not publish a new runtime");
                    helper.assertValueEqual(context.data().getStory(), TRANSACTION_REPLACEMENT,
                            "story id after replacement commit");
                    helper.assertFalse(oldRuntime.get().hasFlow("discarded"),
                            "A lifecycle transaction must discard older flow transactions");
                    helper.assertFalse(runtime.hasFlow("discarded"),
                            "Discarded flow state leaked into the replacement runtime");
                    helper.assertValueEqual(context.data().getVariable("kept", IntStoryValue.class), 7,
                            "persistent script variable");
                })
                .thenExecute(context::close)
                .thenSucceed();
    }

    public static void resetIsDeferredAndFailedReplacementDoesNotDisplaceIt(GameTestHelper helper) {
        var context = startStory(helper, TRANSACTION_BASE);
        var oldRuntime = new AtomicReference<StoryRuntime>();

        helper.startSequence()
                .thenWaitUntil(() -> requireRuntime(helper, context))
                .thenExecute(() -> {
                    context.instance().stop();
                    oldRuntime.set(requireRuntime(helper, context));
                    context.data().setState("state-before-reset");
                    context.data().setEnded(false);
                    context.data().setPendingLine("pending-before-reset", true);
                    context.data().setContinuousToken(UUID.randomUUID());
                    context.data().setVariable("removed", new IntStoryValue(1));

                    context.instance().requestResetStory();

                    helper.assertTrue(context.instance().getRuntime().orElse(null) == oldRuntime.get(),
                            "Reset must not release the runtime in the requesting tick");
                    helper.assertValueEqual(context.data().getStory(), TRANSACTION_BASE,
                            "story id before reset commit");
                    helper.assertValueEqual(context.data().getState(), "state-before-reset",
                            "story state before reset commit");
                    helper.assertTrue(context.data().hasPendingLine("pending-before-reset"),
                            "Reset cleared pending lines before commit");
                    helper.assertTrue(context.data().hasVariable("removed"),
                            "Reset cleared variables before commit");
                    helper.assertTrue(context.data().getContinuousToken() == null,
                            "A queued lifecycle transaction must invalidate the old continuation token immediately");

                    helper.assertFalse(context.instance().requestNewStory(MISSING),
                            "A missing replacement candidate must be rejected");
                })
                .thenWaitUntil(() -> {
                    helper.assertTrue(context.instance().getRuntime().isEmpty(),
                            "The queued reset was displaced by a failed replacement");
                    helper.assertTrue(context.data().getStory() == null, "Reset did not clear the story id");
                    helper.assertTrue(context.data().getState() == null, "Reset did not clear the Ink state");
                    helper.assertTrue(context.data().isEnded(), "Reset did not mark the story ended");
                    helper.assertFalse(context.data().hasPendingLine("pending-before-reset"),
                            "Reset did not clear pending lines");
                    helper.assertTrue(context.data().getContinuousToken() == null,
                            "Reset did not clear the continuation token");
                    helper.assertFalse(context.data().hasVariable("removed"),
                            "Reset did not clear script variables");
                })
                .thenExecute(context::close)
                .thenSucceed();
    }

    public static void stopPreservesQueuedTransactions(GameTestHelper helper) {
        var context = startStory(helper, TRANSACTION_BASE);

        helper.startSequence()
                .thenWaitUntil(() -> requireRuntime(helper, context))
                .thenExecute(() -> {
                    context.instance().stop();
                    helper.assertTrue(context.instance().requestNewFlow("kept", "buy"),
                            "The flow transaction should be accepted");
                    context.instance().stop();
                })
                .thenWaitUntil(() -> {
                    var runtime = requireRuntime(helper, context);
                    helper.assertTrue(runtime.hasFlow("kept"), "stop() discarded a queued transaction");
                })
                .thenExecute(context::close)
                .thenSucceed();
    }

    public static void disposeDropsQueuedTransactionsWithoutClearingData(GameTestHelper helper) {
        var context = startStory(helper, TRANSACTION_BASE);
        var oldRuntime = new AtomicReference<StoryRuntime>();
        var oldState = new AtomicReference<String>();

        helper.startSequence()
                .thenWaitUntil(() -> requireRuntime(helper, context))
                .thenExecute(() -> {
                    context.instance().stop();
                    oldRuntime.set(requireRuntime(helper, context));
                    oldState.set(context.data().getState());

                    helper.assertTrue(context.instance().requestNewFlow("dropped", "buy"),
                            "The flow transaction should be accepted");
                    context.instance().dispose();

                    helper.assertTrue(context.instance().getRuntime().isEmpty(),
                            "dispose() must release the runtime immediately");
                    helper.assertValueEqual(context.data().getStory(), TRANSACTION_BASE,
                            "story id after dispose");
                    helper.assertTrue(Objects.equals(context.data().getState(), oldState.get()),
                            "dispose() modified persisted Ink state");
                })
                .thenIdle(3)
                .thenExecute(() -> {
                    helper.assertFalse(oldRuntime.get().hasFlow("dropped"),
                            "dispose() allowed a queued transaction to run");
                    helper.assertValueEqual(context.data().getStory(), TRANSACTION_BASE,
                            "story id after the disposed transaction wake-up");
                    helper.assertTrue(Objects.equals(context.data().getState(), oldState.get()),
                            "The disposed transaction wake-up modified persisted Ink state");
                })
                .thenExecute(context::close)
                .thenSucceed();
    }

    public static void restoreRuntimeNormalizesPersistedEndedFlag(GameTestHelper helper) {
        var context = startStory(helper, TRANSACTION_BASE);

        helper.startSequence()
                .thenWaitUntil(() -> {
                    var runtime = requireRuntime(helper, context);
                    helper.assertTrue(context.data().getState() != null,
                            "The active story did not save a restorable state");
                    helper.assertTrue(runtime.hasPendingLine(),
                            "The active story did not preserve its pending line");
                })
                .thenExecute(() -> {
                    context.instance().stop();
                    var savedState = context.data().getState();
                    context.data().setEnded(true);

                    context.manager().remove(context.player());
                    var restored = context.manager().get(context.player());

                    helper.assertTrue(restored.getData() == context.data(),
                            "The restored instance did not reuse the persisted player data");
                    helper.assertTrue(restored.getRuntime().isPresent(),
                            "A valid saved state did not restore its runtime");
                    helper.assertFalse(context.data().isEnded(),
                            "A restored runtime retained the persisted ended flag");
                    helper.assertFalse(restored.isStoryEnded(),
                            "The restored runtime is still reported as ended");
                    helper.assertTrue(Objects.equals(context.data().getState(), savedState),
                            "Restoring the runtime modified the saved Ink state");
                    helper.assertTrue(context.data().hasPendingLine(StoryState.kDefaultFlowName),
                            "Restoring the runtime discarded its pending line");

                    restored.start();
                    helper.assertTrue(restored.isStoryRunning(),
                            "The normalized runtime could not resume playback");
                })
                .thenExecute(context::close)
                .thenSucceed();
    }

    public static void restoreMissingStoryPreservesPersistedState(GameTestHelper helper) {
        @SuppressWarnings("removal")
        var player = helper.makeMockServerPlayerInLevel();
        var manager = EngineManager.getInstance();
        var data = new InkPlayerData();
        var savedState = "state-for-missing-story";
        var continuousToken = UUID.randomUUID();
        data.setStory(MISSING);
        data.setState(savedState);
        data.setEnded(false);
        data.setPendingLine(StoryState.kDefaultFlowName, true);
        data.setContinuousToken(continuousToken);

        var instance = new StoryInstance(manager, player, data);
        var context = new TestStory(manager, player, data, instance);

        helper.startSequence()
                .thenExecute(() -> {
                    helper.assertTrue(instance.getRuntime().isEmpty(),
                            "A missing story unexpectedly created a runtime");
                    helper.assertValueEqual(data.getStory(), MISSING,
                            "story id after failed restore");
                    helper.assertValueEqual(data.getState(), savedState,
                            "story state after failed restore");
                    helper.assertFalse(data.isEnded(),
                            "A failed restore marked the persisted story as ended");
                    helper.assertTrue(data.hasPendingLine(StoryState.kDefaultFlowName),
                            "A failed restore cleared the pending line");
                    helper.assertValueEqual(data.getContinuousToken(), continuousToken,
                            "continuous token after failed restore");
                })
                .thenExecute(context::close)
                .thenSucceed();
    }

    public static void currentRecoversMissingRuntimeAndPreservesHealthyRuntime(GameTestHelper helper) {
        var context = startStory(helper, TRANSACTION_BASE);
        var recoveryStoryId = uniqueTestStory("current_recovery");
        var savedState = new AtomicReference<String>();
        var originalRuntime = new AtomicReference<StoryRuntime>();

        helper.startSequence()
                .thenWaitUntil(() -> {
                    var runtime = requireRuntime(helper, context);
                    helper.assertTrue(context.data().getState() != null,
                            "The active story did not save a restorable state");
                    helper.assertTrue(runtime.hasPendingLine(),
                            "The active story did not preserve its pending line");
                })
                .thenExecute(() -> {
                    context.instance().stop();
                    var runtime = requireRuntime(helper, context);
                    originalRuntime.set(runtime);
                    savedState.set(context.data().getState());

                    helper.assertTrue(context.instance().requestResume(),
                            "A healthy runtime rejected current");
                    helper.assertTrue(context.instance().getRuntime().orElse(null) == runtime,
                            "current replaced a healthy runtime");
                    helper.assertTrue(context.instance().isStoryRunning(),
                            "current did not restart a healthy runtime");

                    context.instance().stop();
                    context.manager().remove(context.player());
                    context.data().setStory(recoveryStoryId);
                    var missingRuntimeInstance = context.manager().get(context.player());
                    helper.assertTrue(missingRuntimeInstance.getRuntime().isEmpty(),
                            "A missing story unexpectedly restored its runtime");

                    var token = UUID.randomUUID();
                    context.data().setContinuousToken(token);
                    helper.assertFalse(missingRuntimeInstance.requestResume(),
                            "current accepted recovery while the story was missing");
                    assertPersistedSession(helper, context.data(), recoveryStoryId,
                            savedState.get(), token, "failed missing-story recovery");

                    context.manager().getStoryRegistry().add(recoveryStoryId,
                            requireCompiledStory(TRANSACTION_BASE));
                    helper.assertTrue(missingRuntimeInstance.requestResume(),
                            "current did not recover the story after it reappeared");
                    var recoveredRuntime = missingRuntimeInstance.getRuntime().orElse(null);
                    helper.assertTrue(recoveredRuntime != null,
                            "Successful current recovery did not publish a runtime");
                    helper.assertTrue(recoveredRuntime != originalRuntime.get(),
                            "Successful current recovery reused the disposed runtime");
                    helper.assertTrue(missingRuntimeInstance.isStoryRunning(),
                            "Successful current recovery did not start playback");
                    helper.assertTrue(context.data().getContinuousToken() == null,
                            "Successful current recovery retained a stale continuous token");
                    helper.assertFalse(context.data().isEnded(),
                            "Successful current recovery retained an ended flag");
                    helper.assertValueEqual(context.data().getState(), savedState.get(),
                            "story state after successful current recovery");
                    helper.assertTrue(context.data().hasPendingLine(StoryState.kDefaultFlowName),
                            "Successful current recovery discarded its pending line");
                })
                .thenExecute(context::close)
                .thenSucceed();
    }

    public static void currentRejectsInvalidSavedStateWithoutPublishingRuntime(GameTestHelper helper) {
        @SuppressWarnings("removal")
        var player = helper.makeMockServerPlayerInLevel();
        var manager = EngineManager.getInstance();
        var data = new InkPlayerData();
        var invalidState = "not-valid-ink-state";
        var continuousToken = UUID.randomUUID();
        data.setStory(TRANSACTION_BASE);
        data.setState(invalidState);
        data.setEnded(false);
        data.setPendingLine(StoryState.kDefaultFlowName, true);
        data.setContinuousToken(continuousToken);
        data.setVariable("kept", new IntStoryValue(7));

        var instance = new StoryInstance(manager, player, data);
        var context = new TestStory(manager, player, data, instance);

        helper.startSequence()
                .thenExecute(() -> {
                    helper.assertTrue(instance.getRuntime().isEmpty(),
                            "An invalid state unexpectedly published a runtime during construction");
                    helper.assertFalse(instance.requestResume(),
                            "current accepted an invalid saved state");
                    helper.assertTrue(instance.getRuntime().isEmpty(),
                            "Failed current recovery published a partial runtime");
                    assertPersistedSession(helper, data, TRANSACTION_BASE, invalidState,
                            continuousToken, "failed invalid-state recovery");
                    helper.assertValueEqual(data.getVariable("kept", IntStoryValue.class), 7,
                            "persistent variable after failed invalid-state recovery");
                    helper.assertFalse(instance.isStoryRunning(),
                            "Failed current recovery started playback");
                })
                .thenExecute(context::close)
                .thenSucceed();
    }

    public static void currentCommandsReturnResumeResult(GameTestHelper helper) {
        @SuppressWarnings("removal")
        var self = helper.makeMockServerPlayerInLevel();
        @SuppressWarnings("removal")
        var target = helper.makeMockServerPlayerInLevel();
        var manager = EngineManager.getInstance();
        var selfData = InkraftPlatform.getPlayerData(self);
        var targetData = InkraftPlatform.getPlayerData(target);
        var selfInstance = manager.get(self);
        var targetInstance = manager.get(target);
        var dispatcher = helper.getLevel().getServer().getCommands().getDispatcher();
        var recoveryStoryId = uniqueTestStory("current_command_recovery");
        var targetTag = "inkraft_current_" + UUID.randomUUID().toString().replace("-", "");
        target.addTag(targetTag);
        manager.getStoryRegistry().add(recoveryStoryId, requireCompiledStory(TRANSACTION_BASE));

        helper.startSequence()
                .thenExecute(() -> {
                    var selfSource = self.createCommandSourceStack()
                            .withPermission(LevelBasedPermissionSet.OWNER);
                    var adminSource = helper.getLevel().getServer().createCommandSourceStack()
                            .withPermission(LevelBasedPermissionSet.OWNER);

                    helper.assertValueEqual(executeCommand(dispatcher, "inkraft current", selfSource), 0,
                            "self current result without a saved story");
                    helper.assertValueEqual(executeCommand(dispatcher,
                                    "inkraft current @a[tag=" + targetTag + ",limit=1]", adminSource), 0,
                            "admin current result without a saved story");

                    createRestorableState(selfData, recoveryStoryId);
                    createRestorableState(targetData, recoveryStoryId);

                    helper.assertValueEqual(executeCommand(dispatcher, "inkraft current", selfSource), 1,
                            "self current result with a recoverable story");
                    helper.assertValueEqual(executeCommand(dispatcher,
                                    "inkraft current @a[tag=" + targetTag + ",limit=1]", adminSource), 1,
                            "admin current result with a recoverable story");
                    helper.assertTrue(selfInstance.getRuntime().isPresent(),
                            "self current did not recover its runtime");
                    helper.assertTrue(targetInstance.getRuntime().isPresent(),
                            "admin current did not recover the target runtime");
                })
                .thenExecute(() -> {
                    selfInstance.dispose();
                    targetInstance.dispose();
                    manager.remove(self);
                    manager.remove(target);
                    self.connection.disconnect(Component.literal("Inkraft GameTest complete"));
                    target.connection.disconnect(Component.literal("Inkraft GameTest complete"));
                })
                .thenSucceed();
    }

    public static void pauseDuringContinuePreservesPendingLine(GameTestHelper helper) {
        var context = startStory(helper, TRANSACTION_BASE);

        helper.startSequence()
                .thenWaitUntil(() -> {
                    var runtime = requireRuntime(helper, context);
                    helper.assertTrue(context.data().getContinuousToken() != null,
                            "pause() did not expose manual continuation");
                    helper.assertTrue(runtime.hasPendingLine(),
                            "pause() discarded the line produced by the current Continue call");
                    helper.assertFalse(callChecked(runtime::currentLine).isBlank(),
                            "The pending line produced with pause() is blank");
                    helper.assertFalse(context.instance().isStoryRunning(),
                            "pause() did not stop automatic playback");
                })
                .thenExecute(context::close)
                .thenSucceed();
    }

    public static void resetRequestedDuringContinueCommitsOnNextTick(GameTestHelper helper) {
        var context = startStory(helper, TRANSACTION_BASE);
        var oldRuntime = new AtomicReference<StoryRuntime>();
        var resetRuntime = new AtomicReference<StoryRuntime>();

        helper.startSequence()
                .thenWaitUntil(() -> requireRuntime(helper, context))
                .thenExecute(() -> {
                    context.instance().stop();
                    oldRuntime.set(requireRuntime(helper, context));
                    helper.assertTrue(context.instance().requestNewStory(RESET_DURING_CONTINUE),
                            "Could not enqueue the reset-during-Continue story");
                })
                .thenWaitUntil(() -> {
                    var runtime = requireRuntime(helper, context);
                    helper.assertTrue(runtime != oldRuntime.get(),
                            "The reset-during-Continue runtime has not been published yet");
                    helper.assertValueEqual(context.data().getStory(), RESET_DURING_CONTINUE,
                            "story id before the external reset");
                    if (resetRuntime.compareAndSet(null, runtime)) {
                        bindExternalAction(runtime, "resetStoryForTest",
                                context.instance()::requestResetStory);
                    }
                })
                .thenWaitUntil(() -> {
                    helper.assertTrue(context.instance().getRuntime().isEmpty(),
                            "Reset requested by an external function did not commit");
                    helper.assertTrue(context.data().getStory() == null,
                            "Reset requested during Continue did not clear the story id");
                    helper.assertTrue(context.data().getState() == null,
                            "Reset requested during Continue did not clear the saved state");
                })
                .thenExecute(context::close)
                .thenSucceed();
    }

    public static void runtimeFailureStopsPlaybackAndPreservesSession(GameTestHelper helper) {
        var context = startStory(helper, RUNTIME_FAILURE_DURING_CONTINUE);
        var failedRuntime = new AtomicReference<StoryRuntime>();
        var attempts = new AtomicInteger();
        var continuousToken = UUID.randomUUID();

        helper.startSequence()
                .thenWaitUntil(() -> {
                    var runtime = requireRuntime(helper, context);
                    if (failedRuntime.compareAndSet(null, runtime)) {
                        context.data().setState("state-before-failure");
                        context.data().setEnded(false);
                        context.data().setPendingLine("pending-before-failure", true);
                        context.data().setContinuousToken(continuousToken);
                        context.data().setVariable("kept", new IntStoryValue(7));
                        bindExternalAction(runtime, "failRuntimeForTest", () -> {
                            attempts.incrementAndGet();
                            if (!context.instance().requestFlowTo(StoryState.kDefaultFlowName)) {
                                throw new IllegalStateException("Could not enqueue the failure-path transaction");
                            }
                            throw new Exception("expected GameTest runtime failure");
                        });
                    }
                })
                .thenWaitUntil(() -> {
                    helper.assertValueEqual(attempts.get(), 1, "external function attempts");
                    helper.assertFalse(context.instance().isStoryRunning(),
                            "A runtime failure did not stop automatic playback");
                    helper.assertTrue(context.instance().getRuntime().orElse(null) == failedRuntime.get(),
                            "A runtime failure released the live runtime");
                    helper.assertValueEqual(context.data().getStory(), RUNTIME_FAILURE_DURING_CONTINUE,
                            "story id after runtime failure");
                    helper.assertValueEqual(context.data().getState(), "state-before-failure",
                            "saved state after runtime failure");
                    helper.assertFalse(context.data().isEnded(),
                            "A runtime failure marked the story as ended");
                    helper.assertTrue(context.data().hasPendingLine("pending-before-failure"),
                            "A runtime failure cleared pending-line data");
                    helper.assertValueEqual(context.data().getVariable("kept", IntStoryValue.class), 7,
                            "persistent variable after runtime failure");
                    helper.assertTrue(context.data().getContinuousToken() == null,
                            "A runtime failure did not clear the continuous token");
                })
                .thenIdle(5)
                .thenExecute(() -> {
                    helper.assertValueEqual(attempts.get(), 1,
                            "external function attempts after scheduler wake-ups");
                    context.instance().start();
                    helper.assertTrue(context.instance().isStoryRunning(),
                            "Manual start was blocked by a transaction left behind by the failure");
                    context.instance().stop();
                })
                .thenExecute(context::close)
                .thenSucceed();
    }

    public static void storyInstanceChoiceRestartsAfterNormalResults(GameTestHelper helper) {
        var context = startStory(helper, SYSTEM_FUNCTION_CONTEXT);

        helper.startSequence()
                .thenWaitUntil(() -> requireRuntime(helper, context))
                .thenExecute(() -> {
                    context.instance().stop();
                    var runtime = requireRuntime(helper, context);
                    advanceToChoices(helper, runtime);

                    context.instance().choose(-1);
                    helper.assertTrue(context.instance().isStoryRunning(),
                            "An invalid choice result did not restart choice display");
                    helper.assertTrue(runtime.hasChoice(),
                            "An invalid choice changed the active choices");

                    context.instance().stop();
                    context.instance().choose(1);
                    helper.assertTrue(context.instance().isStoryRunning(),
                            "A successful choice did not restart playback");
                })
                .thenWaitUntil(() -> helper.assertTrue(context.instance().getRuntime().isEmpty(),
                        "The selected terminating choice did not finish the story"))
                .thenExecute(context::close)
                .thenSucceed();
    }

    public static void storyInstanceChoiceFailureDoesNotRestart(GameTestHelper helper) {
        var data = new FailingStateData();
        var context = startStoryWithData(helper, SYSTEM_FUNCTION_CONTEXT, data);
        var failedRuntime = new AtomicReference<StoryRuntime>();
        var savedState = new AtomicReference<String>();

        helper.startSequence()
                .thenWaitUntil(() -> requireRuntime(helper, context))
                .thenExecute(() -> {
                    context.instance().stop();
                    var runtime = requireRuntime(helper, context);
                    failedRuntime.set(runtime);
                    advanceToChoices(helper, runtime);
                    savedState.set(data.getState());
                    data.setContinuousToken(UUID.randomUUID());
                    data.failStateWrites();

                    context.instance().choose(1);

                    helper.assertFalse(context.instance().isStoryRunning(),
                            "A failed choice restarted playback");
                    helper.assertTrue(context.instance().getRuntime().orElse(null) == runtime,
                            "A failed choice released the runtime");
                    helper.assertTrue(Objects.equals(data.getState(), savedState.get()),
                            "A failed choice replaced the saved state");
                    helper.assertFalse(data.isEnded(),
                            "A failed choice marked the story as ended");
                    helper.assertTrue(data.getContinuousToken() == null,
                            "A failed choice did not clear the continuous token");
                })
                .thenIdle(5)
                .thenExecute(() -> {
                    helper.assertFalse(context.instance().isStoryRunning(),
                            "A failed choice was restarted by the scheduler");
                    helper.assertTrue(context.instance().getRuntime().orElse(null) == failedRuntime.get(),
                            "The scheduler released the failed choice runtime");
                })
                .thenExecute(context::close)
                .thenSucceed();
    }

    public static void systemFunctionsUseLiveStoryInstance(GameTestHelper helper) {
        var context = startStory(helper, SYSTEM_FUNCTION_CONTEXT);

        helper.startSequence()
                .thenWaitUntil(() -> requireRuntime(helper, context))
                .thenExecute(() -> {
                    context.instance().stop();
                    var runtime = requireRuntime(helper, context);

                    var isFlowEnded = new IsFlowEndedFunction();
                    context.data().setEnded(true);
                    helper.assertTrue(callChecked(() -> isFlowEnded.apply(context.instance(),
                                    new StringStoryValue(StoryState.kDefaultFlowName))) == BoolStoryValue.FALSE,
                            "isFlowEnded must inspect the named flow instead of the lifecycle flag");
                    context.data().setEnded(false);

                    var setLineTicks = new SetLineTicksFunction();
                    helper.assertTrue(setLineTicks.apply(context.instance(),
                                    new IntStoryValue(-2)) == BoolStoryValue.FALSE,
                            "setLineTicks must reject values below the manual-continuation sentinel");
                    helper.assertFalse(context.data().hasVariable(ModConstants.Variables.LINE_PAUSE_TICKS),
                            "A rejected line tick value must not modify player data");
                    helper.assertTrue(setLineTicks.apply(context.instance(),
                                    new IntStoryValue(-1)) == BoolStoryValue.TRUE,
                            "setLineTicks must accept the manual-continuation sentinel");
                    helper.assertValueEqual(
                            context.data().getVariable(ModConstants.Variables.LINE_PAUSE_TICKS,
                                    IntStoryValue.class),
                            -1, "line pause ticks");

                    var list = new InkList();
                    list.put(new InkListItem("mood", "happy"), 1);
                    var setVariable = new SetVariableFunction();
                    helper.assertTrue(setVariable.apply(context.instance(),
                                    new StringStoryValue("mood"), new InkListStoryValue(list))
                                    == BoolStoryValue.TRUE,
                            "setVariable rejected an Ink list");
                    helper.assertTrue(Objects.equals(context.data().getVariable("mood"),
                                    new StringStoryValue("happy")),
                            "setVariable did not normalize an Ink list immediately");
                    helper.assertTrue(Objects.equals(new GetVariableFunction().apply(context.instance(),
                                            new StringStoryValue("mood")),
                                    new StringStoryValue("happy")),
                            "getVariable did not expose the normalized Ink-list value");

                    while (runtime.canContinueLine()) {
                        helper.assertTrue(callChecked(runtime::continueLine),
                                "Could not advance the system-function test story to its choice");
                        runtime.clearPendingLine();
                    }
                    helper.assertTrue(runtime.hasChoice(),
                            "The system-function test story did not reach its choice");
                    helper.assertTrue(callChecked(() -> runtime.choose(1)),
                            "Could not select the terminating choice");
                    while (runtime.canContinueLine()) {
                        helper.assertTrue(callChecked(runtime::continueLine),
                                "Could not finish the system-function test story");
                        runtime.clearPendingLine();
                    }
                    helper.assertTrue(callChecked(() -> isFlowEnded.apply(context.instance(),
                                    new StringStoryValue(StoryState.kDefaultFlowName))) == BoolStoryValue.TRUE,
                            "isFlowEnded did not report the completed flow");
                })
                .thenExecute(context::close)
                .thenSucceed();
    }

    public static void playerStatFunctionsUseCurrentPlayer(GameTestHelper helper) {
        var context = startStory(helper, PLAYER_STAT_FUNCTIONS);

        helper.startSequence()
                .thenWaitUntil(() -> {
                    var runtime = requireRuntime(helper, context);
                    helper.assertTrue(runtime.hasChoice(),
                            "The player-stat function story did not reach its verification choice");
                })
                .thenExecute(() -> {
                    context.instance().stop();
                })
                .thenWaitUntil(() -> helper.assertTrue(context.player().connection.hasClientLoaded(),
                        "The player-stat test player has not finished its simulated client load"))
                .thenExecute(() -> {
                    var runtime = requireRuntime(helper, context);
                    helper.assertTrue(callChecked(() -> runtime.choose(0)),
                            "Could not select the player-stat verification path");

                    var line = "";
                    while (line.isBlank() && runtime.canContinueLine()) {
                        helper.assertTrue(callChecked(runtime::continueLine),
                                "Could not evaluate the player-stat functions");
                        line = callChecked(runtime::currentLine).trim();
                        runtime.clearPendingLine();
                    }
                    helper.assertValueEqual(line, "PLAYER_STAT_WAIT_FOR_PICKUP",
                            "player-stat pickup wait marker");
                })
                // The embedded GameTest connection does not tick its player, so run the vanilla pickup scan explicitly.
                .thenExecute(context.player()::doTick)
                .thenIdle(3)
                .thenExecute(() -> {
                    var runtime = requireRuntime(helper, context);
                    var line = "";
                    while (line.isBlank() && runtime.canContinueLine()) {
                        helper.assertTrue(callChecked(runtime::continueLine),
                                "Could not verify the changed player statistics");
                        line = callChecked(runtime::currentLine).trim();
                        runtime.clearPendingLine();
                    }
                    helper.assertValueEqual(line, "PLAYER_STAT_OK",
                            "player-stat function result");
                })
                .thenExecute(context::close)
                .thenSucceed();
    }

    public static void uuidFunctionsUseJavaUuidSemantics(GameTestHelper helper) {
        var context = startStory(helper, UUID_FUNCTIONS);

        helper.startSequence()
                .thenWaitUntil(() -> {
                    var runtime = requireRuntime(helper, context);
                    helper.assertTrue(runtime.hasChoice(),
                            "The UUID function story did not reach its verification choice");
                })
                .thenExecute(() -> {
                    context.instance().stop();
                    var runtime = requireRuntime(helper, context);
                    helper.assertTrue(callChecked(() -> runtime.choose(0)),
                            "Could not select the UUID verification path");

                    var line = "";
                    while (line.isBlank() && runtime.canContinueLine()) {
                        helper.assertTrue(callChecked(runtime::continueLine),
                                "Could not evaluate the UUID functions");
                        line = callChecked(runtime::currentLine).trim();
                        runtime.clearPendingLine();
                    }
                    helper.assertValueEqual(line, "UUID_FUNCTIONS_OK",
                            "UUID function result");
                })
                .thenExecute(context::close)
                .thenSucceed();
    }

    public static void deathRespawnRebindsStoryInstance(GameTestHelper helper) {
        respawnRebindsStoryInstance(helper, false, Entity.RemovalReason.KILLED);
    }

    public static void endCreditsRespawnRebindsStoryInstance(GameTestHelper helper) {
        respawnRebindsStoryInstance(helper, true, Entity.RemovalReason.CHANGED_DIMENSION);
    }

    private static void respawnRebindsStoryInstance(GameTestHelper helper, boolean keepEverything,
                                                     Entity.RemovalReason removalReason) {
        var context = startStory(helper, TRANSACTION_BASE);
        var respawnedPlayer = new AtomicReference<ServerPlayer>();
        var respawnedInstance = new AtomicReference<StoryInstance>();
        var savedState = new AtomicReference<String>();
        var token = UUID.randomUUID();
        var pendingFlow = "respawn_pending";
        var discardedFlow = "discarded_on_respawn";

        helper.startSequence()
                .thenWaitUntil(() -> {
                    requireRuntime(helper, context);
                    helper.assertTrue(context.data().getState() != null,
                            "The story did not save state before respawn");
                })
                .thenExecute(() -> {
                    context.instance().stop();
                    context.data().setVariable("respawn_variable", new IntStoryValue(17));
                    context.data().setPendingLine(pendingFlow, true);
                    context.data().setContinuousToken(token);
                    savedState.set(context.data().getState());

                    helper.assertTrue(context.instance().requestNewFlow(discardedFlow, "buy"),
                            "The pre-respawn flow request was not accepted");

                    var newPlayer = helper.getLevel().getServer().getPlayerList()
                            .respawn(context.player(), keepEverything, removalReason);
                    respawnedPlayer.set(newPlayer);

                    helper.assertTrue(newPlayer != context.player(),
                            "PlayerList.respawn reused the old ServerPlayer");
                    helper.assertTrue(context.instance().getRuntime().isEmpty(),
                            "The old StoryInstance was not disposed during respawn");

                    var newData = InkraftPlatform.getPlayerData(newPlayer);
                    helper.assertValueEqual(newData.getStory(), TRANSACTION_BASE,
                            "story id after respawn");
                    helper.assertValueEqual(newData.getState(), savedState.get(),
                            "story state after respawn");
                    helper.assertFalse(newData.isEnded(), "Respawn marked the restored story as ended");
                    helper.assertTrue(newData.hasPendingLine(pendingFlow),
                            "Respawn dropped a persisted pending line");
                    helper.assertValueEqual(newData.getVariable("respawn_variable", IntStoryValue.class), 17,
                            "story variable after respawn");
                    helper.assertTrue(newData.getContinuousToken() == null,
                            "Respawn copied a transient continuous token");

                    var newInstance = context.manager().get(newPlayer);
                    respawnedInstance.set(newInstance);
                    helper.assertTrue(newInstance != context.instance(),
                            "EngineManager retained the old StoryInstance");
                    helper.assertTrue(newInstance.getPlayer() == newPlayer,
                            "The restored StoryInstance is not bound to the new player");
                    helper.assertTrue(newInstance.getRuntime().isPresent(),
                            "The restored StoryInstance has no runtime");
                    helper.assertFalse(newInstance.isStoryRunning(),
                            "The restored story resumed automatically");
                })
                .thenExecuteAfter(2, () -> {
                    var newPlayer = respawnedPlayer.get();
                    var newInstance = respawnedInstance.get();
                    helper.assertTrue(newPlayer != null && newInstance != null,
                            "Respawn did not publish the replacement player and story instance");
                    helper.assertTrue(context.instance().getRuntime().isEmpty(),
                            "A cancelled task revived the old StoryInstance");
                    var runtime = newInstance.getRuntime().orElseThrow();
                    helper.assertFalse(runtime.hasFlow(discardedFlow),
                            "An uncommitted pre-respawn transaction was applied");
                    helper.assertFalse(newInstance.isStoryRunning(),
                            "The restored story started after the respawn tick");
                })
                .thenExecute(context::close)
                .thenSucceed();
    }

    private static TestStory startStory(GameTestHelper helper, Identifier storyId) {
        @SuppressWarnings("removal")
        var player = helper.makeMockServerPlayerInLevel();
        var manager = EngineManager.getInstance();
        var data = InkraftPlatform.getPlayerData(player);
        var instance = manager.get(player);
        helper.assertTrue(instance.requestNewStory(storyId), "Could not enqueue story " + storyId);
        return new TestStory(manager, player, data, instance);
    }

    private static TestStory startStoryWithData(GameTestHelper helper, Identifier storyId,
                                                IInkPlayerData data) {
        @SuppressWarnings("removal")
        var player = helper.makeMockServerPlayerInLevel();
        var manager = EngineManager.getInstance();
        var instance = new StoryInstance(manager, player, data);
        helper.assertTrue(instance.requestNewStory(storyId), "Could not enqueue story " + storyId);
        return new TestStory(manager, player, data, instance);
    }

    private static StoryRuntime requireRuntime(GameTestHelper helper, TestStory context) {
        var runtime = context.instance().getRuntime();
        if (runtime.isEmpty()) {
            helper.fail("The story runtime has not been published yet");
        }
        return runtime.orElseThrow();
    }

    private static void advanceToChoices(GameTestHelper helper, StoryRuntime runtime) {
        while (!runtime.hasChoice()) {
            helper.assertTrue(callChecked(runtime::continueLine),
                    "Could not advance the story to its choices");
            runtime.clearPendingLine();
        }
    }

    private static void assertPersistedSession(GameTestHelper helper, IInkPlayerData data,
                                               Identifier storyId, String savedState,
                                               UUID continuousToken, String operation) {
        helper.assertValueEqual(data.getStory(), storyId, "story id after " + operation);
        helper.assertValueEqual(data.getState(), savedState, "story state after " + operation);
        helper.assertFalse(data.isEnded(), operation + " marked the story as ended");
        helper.assertTrue(data.hasPendingLine(StoryState.kDefaultFlowName),
                operation + " cleared the pending line");
        helper.assertValueEqual(data.getContinuousToken(), continuousToken,
                "continuous token after " + operation);
    }

    private static String requireCompiledStory(Identifier storyId) {
        var compiledStory = EngineManager.getInstance().getStoryRegistry().get(storyId);
        if (compiledStory == null) {
            throw new IllegalStateException("Missing compiled GameTest story " + storyId);
        }
        return compiledStory;
    }

    private static void createRestorableState(IInkPlayerData data, Identifier storyId) {
        try {
            var runtime = new StoryRuntime(data, requireCompiledStory(TRANSACTION_BASE));
            runtime.saveState();
            data.setStory(storyId);
            data.setEnded(false);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not create a restorable current-command state", ex);
        }
    }

    private static int executeCommand(CommandDispatcher<CommandSourceStack> dispatcher, String command,
                                      CommandSourceStack source) {
        try {
            return dispatcher.execute(command, source);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not execute GameTest command /" + command, ex);
        }
    }

    private static Identifier testStory(String name) {
        return Identifier.fromNamespaceAndPath("testmod", name);
    }

    private static Identifier uniqueTestStory(String prefix) {
        return testStory(prefix + "_" + UUID.randomUUID().toString().replace("-", ""));
    }

    private static void bindExternalAction(StoryRuntime runtime, String name, ThrowingAction action) {
        try {
            var accessor = StoryRuntime.class.getDeclaredMethod("getStory");
            // Keep the production accessor package-private; this bridge only exists in the GameTest source set.
            accessor.setAccessible(true);
            var story = (Story) accessor.invoke(runtime);
            story.bindExternalFunction(name, args -> {
                action.run();
                return false;
            }, false);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not bind GameTest external function " + name, ex);
        }
    }

    private static <T> T callChecked(ThrowingSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception ex) {
            throw new IllegalStateException("Unexpected exception in GameTest assertion", ex);
        }
    }

    @FunctionalInterface
    private interface ThrowingAction {
        void run() throws Exception;
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    private static final class FailingStateData extends InkPlayerData {
        private final IllegalStateException failure =
                new IllegalStateException("expected GameTest choice save failure");
        private boolean failStateWrites;

        private void failStateWrites() {
            failStateWrites = true;
        }

        @Override
        public void setState(String state) {
            if (failStateWrites) {
                throw failure;
            }
            super.setState(state);
        }
    }

    private record TestStory(EngineManager manager, ServerPlayer player, IInkPlayerData data,
                             StoryInstance instance) implements AutoCloseable {
        @Override
        public void close() {
            instance.dispose();
            manager.remove(player);
            if (player.connection != null) {
                player.connection.disconnect(Component.literal("Inkraft GameTest complete"));
            }
        }
    }
}
