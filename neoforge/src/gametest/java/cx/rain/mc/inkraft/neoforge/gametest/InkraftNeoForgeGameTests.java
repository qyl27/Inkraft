package cx.rain.mc.inkraft.neoforge.gametest;

import com.mojang.serialization.MapCodec;
import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.gametest.StoryInstanceGameTests;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

import java.util.List;
import java.util.function.Consumer;

@EventBusSubscriber(modid = Inkraft.MODID)
public final class InkraftNeoForgeGameTests {
    private InkraftNeoForgeGameTests() {
    }

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        var environment = event.registerEnvironment(id("environment"),
                new TestEnvironmentDefinition.AllOf(List.of()));

        register(event, environment, "flow_batch_is_fifo_and_beats_pause", 120,
                StoryInstanceGameTests::flowBatchIsFifoAndBeatsPause);
        register(event, environment, "lifecycle_last_write_wins", 120,
                StoryInstanceGameTests::lifecycleLastWriteWins);
        register(event, environment, "reset_is_deferred_and_failed_replacement_does_not_displace_it", 120,
                StoryInstanceGameTests::resetIsDeferredAndFailedReplacementDoesNotDisplaceIt);
        register(event, environment, "stop_preserves_queued_transactions", 120,
                StoryInstanceGameTests::stopPreservesQueuedTransactions);
        register(event, environment, "dispose_drops_queued_transactions_without_clearing_data", 120,
                StoryInstanceGameTests::disposeDropsQueuedTransactionsWithoutClearingData);
        register(event, environment, "restore_runtime_normalizes_persisted_ended_flag", 160,
                StoryInstanceGameTests::restoreRuntimeNormalizesPersistedEndedFlag);
        register(event, environment, "restore_missing_story_preserves_persisted_state", 120,
                StoryInstanceGameTests::restoreMissingStoryPreservesPersistedState);
        register(event, environment, "current_recovers_missing_runtime_and_preserves_healthy_runtime", 160,
                StoryInstanceGameTests::currentRecoversMissingRuntimeAndPreservesHealthyRuntime);
        register(event, environment, "current_rejects_invalid_saved_state_without_publishing_runtime", 120,
                StoryInstanceGameTests::currentRejectsInvalidSavedStateWithoutPublishingRuntime);
        register(event, environment, "current_commands_return_resume_result", 120,
                StoryInstanceGameTests::currentCommandsReturnResumeResult);
        register(event, environment, "pause_during_continue_preserves_pending_line", 160,
                StoryInstanceGameTests::pauseDuringContinuePreservesPendingLine);
        register(event, environment, "reset_requested_during_continue_commits_on_next_tick", 120,
                StoryInstanceGameTests::resetRequestedDuringContinueCommitsOnNextTick);
        register(event, environment, "runtime_failure_stops_playback_and_preserves_session", 160,
                StoryInstanceGameTests::runtimeFailureStopsPlaybackAndPreservesSession);
        register(event, environment, "story_instance_choice_restarts_after_normal_results", 160,
                StoryInstanceGameTests::storyInstanceChoiceRestartsAfterNormalResults);
        register(event, environment, "story_instance_choice_failure_does_not_restart", 160,
                StoryInstanceGameTests::storyInstanceChoiceFailureDoesNotRestart);
        register(event, environment, "system_functions_use_live_story_instance", 200,
                StoryInstanceGameTests::systemFunctionsUseLiveStoryInstance);
        register(event, environment, "death_respawn_rebinds_story_instance", 200,
                StoryInstanceGameTests::deathRespawnRebindsStoryInstance);
        register(event, environment, "end_credits_respawn_rebinds_story_instance", 200,
                StoryInstanceGameTests::endCreditsRespawnRebindsStoryInstance);
    }

    private static void register(RegisterGameTestsEvent event,
                                 Holder<TestEnvironmentDefinition<?>> environment,
                                 String name, int maxTicks, Consumer<GameTestHelper> test) {
        var data = new TestData<>(environment, Identifier.withDefaultNamespace("empty"), maxTicks,
                0, true, Rotation.NONE, false, 1, 1, false, 1);
        event.registerTest(id(name), new DirectGameTestInstance(data, test));
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Inkraft.MODID, path);
    }

    private static final class DirectGameTestInstance extends GameTestInstance {
        private final Consumer<GameTestHelper> test;

        private DirectGameTestInstance(TestData<Holder<TestEnvironmentDefinition<?>>> data,
                                       Consumer<GameTestHelper> test) {
            super(data);
            this.test = test;
        }

        @Override
        public void run(GameTestHelper helper) {
            test.accept(helper);
        }

        @Override
        public MapCodec<? extends GameTestInstance> codec() {
            return MapCodec.unit(this);
        }

        @Override
        protected MutableComponent typeDescription() {
            return Component.literal("Inkraft shared GameTest");
        }
    }
}
