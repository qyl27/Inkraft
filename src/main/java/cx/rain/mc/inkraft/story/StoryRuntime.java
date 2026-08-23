package cx.rain.mc.inkraft.story;

import com.bladecoder.ink.runtime.Choice;
import com.bladecoder.ink.runtime.Story;
import com.bladecoder.ink.runtime.StoryState;
import cx.rain.mc.inkraft.api.platform.storage.IInkPlayerData;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class StoryRuntime {
    private final IInkPlayerData data;
    private final Story story;

    public StoryRuntime(IInkPlayerData data, String compiledJson) throws Exception {
        this.data = data;
        story = new Story(compiledJson);
        story.onError = StoryErrorHandler.INSTANCE;
    }

    Story getStory() {
        return story;
    }

    public boolean hasPendingLine() {
        return data.hasPendingLine(getFlowName());
    }

    public void clearPendingLine() {
        data.setPendingLine(getFlowName(), false);
    }

    boolean applyFlowTransactions(List<StoryTransaction.Flow> transactions) throws Exception {
        if (transactions.isEmpty()) {
            return false;
        }

        var applied = false;
        for (var transaction : transactions) {
            try {
                switch (applyFlowTransaction(transaction)) {
                    case APPLIED -> applied = true;
                    case INVALID -> log.warn("Flow transaction is no longer valid: {}", transaction);
                    case NO_OP -> {
                    }
                }
            } catch (Exception ex) {
                log.error("Failed to apply flow transaction {}", transaction, ex);
            }
        }

        if (applied) {
            saveState();
        }
        return applied;
    }

    private FlowTransactionResult applyFlowTransaction(StoryTransaction.Flow transaction) throws Exception {
        return switch (transaction) {
            case StoryTransaction.NewFlow request -> {
                story.switchFlow(request.name());
                story.choosePathString(request.knot());
                data.setPendingLine(getFlowName(), false);
                yield FlowTransactionResult.APPLIED;
            }
            case StoryTransaction.FlowTo request -> {
                if (!hasFlow(request.name())) {
                    yield FlowTransactionResult.INVALID;
                }
                if (getFlowName().equals(request.name())) {
                    yield FlowTransactionResult.NO_OP;
                }
                switchFlowExisting(request.name());
                yield FlowTransactionResult.APPLIED;
            }
            case StoryTransaction.RemoveFlow request -> {
                if (!hasRemovableFlow(request.name())) {
                    yield FlowTransactionResult.INVALID;
                }
                story.removeFlow(request.name());
                removePendingLine(request.name());
                yield FlowTransactionResult.APPLIED;
            }
        };
    }

    private enum FlowTransactionResult {
        APPLIED,
        NO_OP,
        INVALID
    }

    public void saveState() throws Exception {
        data.setState(story.getState().toJson());
    }

    public void loadState(String json) throws Exception {
        story.getState().loadJson(json);
    }

    public String currentLine() throws Exception {
        return story.getCurrentText();
    }

    public boolean canContinueLine() {
        return story.canContinue();
    }

    public boolean hasChoice() {
        return !story.getCurrentChoices().isEmpty();
    }

    public boolean isCurrentFlowEnded() {
        return !canContinueLine() && !hasChoice();
    }

    public boolean continueLine() throws Exception {
        if (hasPendingLine()) {
            log.warn("continueLine: Current line is still pending display.");
            return false;
        }
        if (!canContinueLine()) {
            log.warn("continueLine: Story can't continue.");
            return false;
        }

        story.Continue();
        data.setPendingLine(getFlowName(), true);
        saveState();
        return true;
    }

    public boolean choose(int index) throws Exception {
        if (index < 0 || index >= story.getCurrentChoices().size()) {
            return false;
        }

        story.chooseChoiceIndex(index);
        data.setPendingLine(getFlowName(), false);
        saveState();
        return true;
    }

    public List<Choice> getChoices() {
        return story.getCurrentChoices();
    }

    public boolean hasFlow(String name) {
        return StoryState.kDefaultFlowName.equals(name) || story.aliveFlowNames().contains(name);
    }

    public boolean isFlowEnded(String name) {
        if (!hasFlow(name)) {
            return true;
        }

        var original = getFlowName();
        if (original.equals(name)) {
            return isCurrentFlowEnded();
        }

        try {
            switchFlowExisting(name);
            try {
                return isCurrentFlowEnded();
            } finally {
                switchFlowExisting(original);
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

    public String getFlowName() {
        return story.getCurrentFlowName();
    }

    boolean hasRemovableFlow(String name) {
        return !StoryState.kDefaultFlowName.equals(name) && hasFlow(name);
    }

    private void switchFlowExisting(String name) throws Exception {
        if (StoryState.kDefaultFlowName.equals(name)) {
            story.switchToDefaultFlow();
        } else {
            story.switchFlow(name);
        }
    }

    private void removePendingLine(String name) {
        data.removePendingLine(name);
    }

}
