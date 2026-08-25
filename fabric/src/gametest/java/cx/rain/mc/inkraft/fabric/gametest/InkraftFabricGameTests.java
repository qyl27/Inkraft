package cx.rain.mc.inkraft.fabric.gametest;

import cx.rain.mc.inkraft.gametest.StoryInstanceGameTests;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public final class InkraftFabricGameTests {
    @GameTest(maxTicks = 120)
    public void flowBatchIsFifoAndBeatsPause(GameTestHelper helper) {
        StoryInstanceGameTests.flowBatchIsFifoAndBeatsPause(helper);
    }

    @GameTest(maxTicks = 120)
    public void lifecycleLastWriteWins(GameTestHelper helper) {
        StoryInstanceGameTests.lifecycleLastWriteWins(helper);
    }

    @GameTest(maxTicks = 120)
    public void resetIsDeferredAndFailedReplacementDoesNotDisplaceIt(GameTestHelper helper) {
        StoryInstanceGameTests.resetIsDeferredAndFailedReplacementDoesNotDisplaceIt(helper);
    }

    @GameTest(maxTicks = 120)
    public void stopPreservesQueuedTransactions(GameTestHelper helper) {
        StoryInstanceGameTests.stopPreservesQueuedTransactions(helper);
    }

    @GameTest(maxTicks = 120)
    public void disposeDropsQueuedTransactionsWithoutClearingData(GameTestHelper helper) {
        StoryInstanceGameTests.disposeDropsQueuedTransactionsWithoutClearingData(helper);
    }

    @GameTest(maxTicks = 160)
    public void restoreRuntimeNormalizesPersistedEndedFlag(GameTestHelper helper) {
        StoryInstanceGameTests.restoreRuntimeNormalizesPersistedEndedFlag(helper);
    }

    @GameTest(maxTicks = 120)
    public void restoreMissingStoryPreservesPersistedState(GameTestHelper helper) {
        StoryInstanceGameTests.restoreMissingStoryPreservesPersistedState(helper);
    }

    @GameTest(maxTicks = 160)
    public void currentRecoversMissingRuntimeAndPreservesHealthyRuntime(GameTestHelper helper) {
        StoryInstanceGameTests.currentRecoversMissingRuntimeAndPreservesHealthyRuntime(helper);
    }

    @GameTest(maxTicks = 120)
    public void currentRejectsInvalidSavedStateWithoutPublishingRuntime(GameTestHelper helper) {
        StoryInstanceGameTests.currentRejectsInvalidSavedStateWithoutPublishingRuntime(helper);
    }

    @GameTest(maxTicks = 120)
    public void currentCommandsReturnResumeResult(GameTestHelper helper) {
        StoryInstanceGameTests.currentCommandsReturnResumeResult(helper);
    }

    @GameTest(maxTicks = 160)
    public void pauseDuringContinuePreservesPendingLine(GameTestHelper helper) {
        StoryInstanceGameTests.pauseDuringContinuePreservesPendingLine(helper);
    }

    @GameTest(maxTicks = 120)
    public void resetRequestedDuringContinueCommitsOnNextTick(GameTestHelper helper) {
        StoryInstanceGameTests.resetRequestedDuringContinueCommitsOnNextTick(helper);
    }

    @GameTest(maxTicks = 160)
    public void runtimeFailureStopsPlaybackAndPreservesSession(GameTestHelper helper) {
        StoryInstanceGameTests.runtimeFailureStopsPlaybackAndPreservesSession(helper);
    }

    @GameTest(maxTicks = 160)
    public void storyInstanceChoiceRestartsAfterNormalResults(GameTestHelper helper) {
        StoryInstanceGameTests.storyInstanceChoiceRestartsAfterNormalResults(helper);
    }

    @GameTest(maxTicks = 160)
    public void storyInstanceChoiceFailureDoesNotRestart(GameTestHelper helper) {
        StoryInstanceGameTests.storyInstanceChoiceFailureDoesNotRestart(helper);
    }

    @GameTest(maxTicks = 200)
    public void systemFunctionsUseLiveStoryInstance(GameTestHelper helper) {
        StoryInstanceGameTests.systemFunctionsUseLiveStoryInstance(helper);
    }

    @GameTest(maxTicks = 200)
    public void deathRespawnRebindsStoryInstance(GameTestHelper helper) {
        StoryInstanceGameTests.deathRespawnRebindsStoryInstance(helper);
    }

    @GameTest(maxTicks = 200)
    public void endCreditsRespawnRebindsStoryInstance(GameTestHelper helper) {
        StoryInstanceGameTests.endCreditsRespawnRebindsStoryInstance(helper);
    }
}
