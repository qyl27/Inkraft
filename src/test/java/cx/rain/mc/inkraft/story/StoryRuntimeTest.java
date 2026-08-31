package cx.rain.mc.inkraft.story;

import com.bladecoder.ink.runtime.Story;
import com.bladecoder.ink.runtime.StoryState;
import cx.rain.mc.inkraft.storage.InkPlayerData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class StoryRuntimeTest {
    private static final int MAX_STORY_STEPS = 100;

    @Test
    void continueLineCreatesPendingCurrentLineUntilMarkedDisplayed() throws Exception {
        var data = new InkPlayerData();
        var runtime = runtimeWithStory(data, "test3");

        assertFalse(runtime.hasPendingLine());
        assertTrue(runtime.continueLine());

        assertTrue(runtime.hasPendingLine());
        assertFalse(runtime.currentLine().isBlank());
        assertNotNull(data.getState());

        runtime.clearPendingLine();

        assertFalse(runtime.hasPendingLine());
    }

    @Test
    void refusesToContinueWhenCurrentLineIsStillPending() throws Exception {
        var data = new InkPlayerData();
        var runtime = runtimeWithStory(data, "test3");

        assertTrue(runtime.continueLine());
        var pending = runtime.currentLine();

        assertFalse(runtime.continueLine());

        assertEquals(pending, runtime.currentLine());
    }

    @Test
    void finalLineCanBePendingEvenWhenCurrentFlowEnded() throws Exception {
        var data = new InkPlayerData();
        var runtime = runtimeWithStory(data, "test3");

        for (int steps = 0; runtime.canContinueLine(); steps++) {
            assertWithinStepLimit(runtime, steps);
            assertTrue(runtime.continueLine());
            if (runtime.canContinueLine()) {
                runtime.clearPendingLine();
            }
        }

        assertTrue(runtime.hasPendingLine());
        assertFalse(runtime.currentLine().isBlank());
        assertTrue(runtime.isCurrentFlowEnded());
    }

    @Test
    void choiceOnlySelectsAndLetsNextTickContinue() throws Exception {
        var data = new InkPlayerData();
        var runtime = runtimeWithStory(data, "test4");

        advanceToChoice(runtime);
        assertFalse(runtime.canContinueLine());

        assertFalse(runtime.choose(-1));
        assertFalse(runtime.choose(runtime.getChoices().size()));
        assertTrue(runtime.choose(1));

        assertFalse(runtime.hasPendingLine());
        assertTrue(runtime.canContinueLine());
    }

    @Test
    void choicePropagatesUnexpectedSaveFailure() throws Exception {
        var failSaves = new AtomicBoolean();
        var failure = new IllegalStateException("expected save failure");
        var data = new InkPlayerData() {
            @Override
            public void setState(String state) {
                if (failSaves.get()) {
                    throw failure;
                }
                super.setState(state);
            }
        };
        var runtime = runtimeWithStory(data, "test4");

        advanceToChoice(runtime);

        failSaves.set(true);
        assertSame(failure, assertThrows(IllegalStateException.class, () -> runtime.choose(1)));
    }

    @Test
    void externalFunctionFailurePropagatesThroughContinueLine() throws Exception {
        var data = new InkPlayerData();
        var json = StoryTestSupport.readStory("test4");
        var runtime = new StoryRuntime(data, json);
        var failure = new Exception("expected external function failure");

        StoryTestSupport.bindExternals(runtime.getStory(), json,
            StoryTestSupport.override("hasItem", _ -> {
                throw failure;
            }));

        advanceToChoice(runtime);
        assertTrue(runtime.choose(0));

        assertSame(failure, assertThrows(Exception.class, runtime::continueLine));
    }

    @Test
    void flowToMissingFlowFailsAtApplyTimeWithoutCreatingIt() throws Exception {
        var data = new InkPlayerData();
        var runtime = runtimeWithStory(data, "test3");

        assertFalse(runtime.hasFlow("side"));
        assertFalse(runtime.applyFlowTransactions(List.of(
            new StoryTransaction.FlowTo("side"),
            new StoryTransaction.RemoveFlow(StoryState.kDefaultFlowName))));
        assertFalse(runtime.hasFlow("side"));
        assertNull(data.getState());
    }

    @Test
    void flowToCurrentFlowIsNoOpWithoutSavingState() throws Exception {
        var data = new InkPlayerData();
        var runtime = runtimeWithStory(data, "test3");

        assertEquals(StoryState.kDefaultFlowName, runtime.getFlowName());
        assertFalse(runtime.applyFlowTransactions(List.of(
            new StoryTransaction.FlowTo(StoryState.kDefaultFlowName))));
        assertEquals(StoryState.kDefaultFlowName, runtime.getFlowName());
        assertNull(data.getState());
    }

    @Test
    void appliesMultipleFlowSwitchRequestsInOrder() throws Exception {
        var data = new InkPlayerData();
        var runtime = runtimeWithStory(data, "test4");

        assertTrue(runtime.applyFlowTransactions(List.of(
            new StoryTransaction.NewFlow("first", "buy"),
            new StoryTransaction.NewFlow("second", "buy"),
            new StoryTransaction.FlowTo(StoryState.kDefaultFlowName))));
        assertTrue(runtime.hasFlow("first"));
        assertTrue(runtime.hasFlow("second"));
        assertEquals(StoryState.kDefaultFlowName, runtime.getFlowName());
        assertFalse(runtime.applyFlowTransactions(List.of()));
    }

    @Test
    void failedFlowTransactionDoesNotBlockLaterValidTransaction() throws Exception {
        var data = new InkPlayerData();
        var runtime = runtimeWithStory(data, "test4");

        assertTrue(runtime.applyFlowTransactions(List.of(
            new StoryTransaction.NewFlow("broken", "missing_knot"),
            new StoryTransaction.FlowTo(StoryState.kDefaultFlowName))));

        assertTrue(runtime.hasFlow("broken"));
        assertEquals(StoryState.kDefaultFlowName, runtime.getFlowName());
        assertNotNull(data.getState());
    }

    @Test
    void defaultFlowHelpersAreImplementedInInkUsingGenericExternals() throws Exception {
        var data = new InkPlayerData();
        var json = StoryTestSupport.readStory("test4");
        var runtime = new StoryRuntime(data, json);
        var story = runtime.getStory();
        var transactions = new ArrayList<StoryTransaction.Flow>();
        bindEngineExternals(story, runtime, json, transactions);

        assertNotNull(story);
        assertTrue(story.hasFunction("isInDefaultFlow"));
        assertTrue(story.hasFunction("flowToDefault"));
        assertNull(story.getExternalFunction("isInDefaultFlow"));
        assertNull(story.getExternalFunction("flowToDefault"));
        assertEquals(true, story.evaluateFunction("isInDefaultFlow"));

        assertTrue(runtime.applyFlowTransactions(List.of(new StoryTransaction.NewFlow("side", "buy"))));
        assertEquals("side", runtime.getFlowName());
        assertEquals(false, story.evaluateFunction("isInDefaultFlow"));

        assertEquals(true, story.evaluateFunction("flowToDefault"));
        assertTrue(runtime.applyFlowTransactions(List.copyOf(transactions)));
        transactions.clear();
        assertEquals(StoryState.kDefaultFlowName, runtime.getFlowName());
        assertEquals(true, story.evaluateFunction("isInDefaultFlow"));
    }

    @Test
    void pendingLineIsTrackedPerFlow() throws Exception {
        var data = new InkPlayerData();
        var runtime = runtimeWithStory(data, "test4");

        assertTrue(runtime.continueLine());
        assertTrue(data.hasPendingLine(StoryState.kDefaultFlowName));

        assertTrue(runtime.applyFlowTransactions(List.of(new StoryTransaction.NewFlow("side", "buy"))));

        assertEquals("side", runtime.getFlowName());
        assertFalse(runtime.hasPendingLine());
        assertTrue(data.hasPendingLine(StoryState.kDefaultFlowName));

        assertTrue(runtime.applyFlowTransactions(List.of(
            new StoryTransaction.FlowTo(StoryState.kDefaultFlowName))));

        assertEquals(StoryState.kDefaultFlowName, runtime.getFlowName());
        assertTrue(runtime.hasPendingLine());
    }

    @Test
    void isFlowEndedQueriesNamedFlowAndRestoresCurrentFlow() throws Exception {
        var data = new InkPlayerData();
        var runtime = runtimeWithStory(data, "test4");

        assertFalse(runtime.isFlowEnded(StoryState.kDefaultFlowName));
        assertTrue(runtime.isFlowEnded("missing"));
        assertFalse(runtime.hasFlow("missing"));

        assertTrue(runtime.applyFlowTransactions(List.of(new StoryTransaction.NewFlow("side", "buy"))));
        assertEquals("side", runtime.getFlowName());

        for (int steps = 0; runtime.canContinueLine(); steps++) {
            assertWithinStepLimit(runtime, steps);
            assertTrue(runtime.continueLine());
            runtime.clearPendingLine();
        }

        assertTrue(runtime.isFlowEnded("side"));
        assertEquals("side", runtime.getFlowName());

        assertTrue(runtime.applyFlowTransactions(List.of(
            new StoryTransaction.FlowTo(StoryState.kDefaultFlowName))));
        assertEquals(StoryState.kDefaultFlowName, runtime.getFlowName());
        assertFalse(runtime.isFlowEnded(StoryState.kDefaultFlowName));
        assertEquals(StoryState.kDefaultFlowName, runtime.getFlowName());
        assertTrue(runtime.isFlowEnded("side"));
    }

    @Test
    void isFlowEndedWrapsBladeCheckedFailureAsIllegalStateException() throws Exception {
        var data = new InkPlayerData();
        var runtime = runtimeWithStory(data, "test4");

        assertTrue(runtime.applyFlowTransactions(List.of(new StoryTransaction.NewFlow("side", "buy"))));
        assertTrue(runtime.applyFlowTransactions(List.of(
            new StoryTransaction.FlowTo(StoryState.kDefaultFlowName))));

        runtime.getStory().copyStateForBackgroundThreadSave();
        try {
            var exception = assertThrows(IllegalStateException.class, () -> runtime.isFlowEnded("side"));

            assertNotNull(exception.getCause());
            assertEquals(Exception.class, exception.getCause().getClass());
            assertEquals(exception.getCause().getMessage(), exception.getMessage());
            assertEquals(StoryState.kDefaultFlowName, runtime.getFlowName());
        } finally {
            runtime.getStory().backgroundSaveComplete();
        }
    }

    @Test
    void newRuntimeOwnsIndependentBladeStory() throws Exception {
        var data = new InkPlayerData();
        var first = runtimeWithStory(data, "test4");
        assertTrue(first.applyFlowTransactions(List.of(new StoryTransaction.NewFlow("side", "buy"))));

        var second = runtimeWithStory(data, "test4");

        assertFalse(second.hasFlow("side"));
        assertNotSame(first.getStory(), second.getStory());
    }

    @Test
    void loadsSavedStateIntoFreshRuntime() throws Exception {
        var data = new InkPlayerData();
        var first = runtimeWithStory(data, "test3");
        assertTrue(first.continueLine());
        var savedLine = first.currentLine();
        var savedState = data.getState();
        assertNotNull(savedState);

        var second = runtimeWithStory(data, "test3");
        second.loadState(savedState);

        assertNotSame(first, second);
        assertEquals(savedLine, second.currentLine());
    }

    @Test
    void malformedSavedStateIsPropagated() throws Exception {
        var runtime = runtimeWithStory(new InkPlayerData(), "test3");

        assertThrows(Exception.class, () -> runtime.loadState("{not valid json"));
    }

    private static StoryRuntime runtimeWithStory(InkPlayerData data, String name) throws Exception {
        var json = StoryTestSupport.readStory(name);
        var runtime = new StoryRuntime(data, json);
        var story = runtime.getStory();
        bindEngineExternals(story, runtime, json, new ArrayList<>());
        return runtime;
    }

    private static void bindEngineExternals(Story story, StoryRuntime runtime,
                                            String json,
                                            List<StoryTransaction.Flow> transactions) throws Exception {
        StoryTestSupport.bindExternals(story, json,
            StoryTestSupport.override("isInFlow",
                args -> runtime.getFlowName().equals(String.valueOf(args[0]))),
            StoryTestSupport.override("flowTo", args -> {
                transactions.add(new StoryTransaction.FlowTo(String.valueOf(args[0])));
                return true;
            }));
    }

    private static void advanceToChoice(StoryRuntime runtime) throws Exception {
        for (int steps = 0; !runtime.hasChoice(); steps++) {
            assertWithinStepLimit(runtime, steps);
            assertTrue(runtime.continueLine());
            runtime.clearPendingLine();
        }
    }

    private static void assertWithinStepLimit(StoryRuntime runtime, int steps) {
        assertTrue(steps < MAX_STORY_STEPS, () -> "Story did not settle within "
            + MAX_STORY_STEPS + " steps: flow=" + runtime.getFlowName()
            + ", pendingLine=" + runtime.hasPendingLine()
            + ", choices=" + runtime.getChoices().size()
            + ", canContinue=" + runtime.canContinueLine());
    }
}
