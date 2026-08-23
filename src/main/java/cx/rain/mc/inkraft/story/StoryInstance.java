package cx.rain.mc.inkraft.story;

import com.bladecoder.ink.runtime.Story;
import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.api.platform.storage.IInkPlayerData;
import cx.rain.mc.inkraft.engine.EngineManager;
import cx.rain.mc.inkraft.registry.InkraftRegistries;
import cx.rain.mc.inkraft.story.function.FunctionSyntaxException;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.timer.cancellation.CancellableToken;
import cx.rain.mc.inkraft.utility.TextStyleHelper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Getter
public class StoryInstance {
    private final EngineManager manager;

    private final ServerPlayer player;
    private final IInkPlayerData data;

    @Nullable
    private StoryRuntime runtime;

    @Getter(AccessLevel.NONE)
    private final Deque<StoryTransaction> transactions = new ArrayDeque<>();

    @Getter(AccessLevel.NONE)
    private boolean disposed;

    public StoryInstance(EngineManager manager, ServerPlayer player, IInkPlayerData data) {
        this.manager = manager;
        this.player = player;
        this.data = data;
        restoreRuntime();
    }

    public Optional<StoryRuntime> getRuntime() {
        return Optional.ofNullable(runtime);
    }

    // region Init

    public boolean requestNewStory(Identifier path) {
        if (disposed) {
            return false;
        }

        try {
            var nextRuntime = createRuntime(path);
            if (nextRuntime == null) {
                return false;
            }
            bindStoryFunctions(nextRuntime.getStory());

            requestLifecycleTransaction(new StoryTransaction.Replace(path, nextRuntime));
            return true;
        } catch (Exception ex) {
            reportStoryFailure("start story", path, ex);
            return false;
        }
    }

    private boolean restoreRuntime() {
        if (!data.hasData()) {
            return false;
        }

        var storyId = data.getStory();
        if (storyId == null) {
            return false;
        }

        var savedState = data.getState();
        if (savedState == null) {
            return false;
        }

        try {
            var nextRuntime = createRuntime(storyId);
            if (nextRuntime == null) {
                log.warn("Cannot restore story {} because it is not currently available; persisted state was retained.",
                        storyId);
                return false;
            }
            nextRuntime.loadState(savedState);
            bindStoryFunctions(nextRuntime.getStory());

            data.setEnded(false);
            data.setContinuousToken(null);
            runtime = nextRuntime;
            return true;
        } catch (Exception ex) {
            reportStoryFailure("restore story", storyId, ex);
            return false;
        }
    }

    @Nullable
    private StoryRuntime createRuntime(Identifier path) throws Exception {
        var compiledJson = manager.getStoryRegistry().get(path);
        if (compiledJson == null) {
            player.sendSystemMessage(Component.translatable(ModConstants.Messages.STORY_NOT_FOUND, path.toString()).withStyle(ChatFormatting.RED));
            return null;
        }

        return new StoryRuntime(data, compiledJson);
    }

    // endregion

    // region Game control

    public boolean requestResume() {
        if (disposed) {
            return false;
        }
        if (runtime == null) {
            if (!transactions.isEmpty() || !restoreRuntime()) {
                return false;
            }
        }

        stop();
        start();
        return true;
    }

    public void requestPause() {
        if (runtime != null && !data.isEnded() && !disposed) {
            enqueueTransaction(StoryTransaction.Pause.INSTANCE);
        }
    }

    public boolean requestNewFlow(String name, String knot) {
        if (runtime == null || data.isEnded() || disposed) {
            return false;
        }

        enqueueTransaction(new StoryTransaction.NewFlow(name, knot));
        return true;
    }

    public boolean requestFlowTo(String name) {
        if (runtime == null || data.isEnded() || disposed) {
            return false;
        }

        enqueueTransaction(new StoryTransaction.FlowTo(name));
        return true;
    }

    public boolean requestRemoveFlow(String name) {
        if (runtime == null || data.isEnded() || disposed) {
            return false;
        }

        enqueueTransaction(new StoryTransaction.RemoveFlow(name));
        return true;
    }

    public void requestResetStory() {
        if (!disposed) {
            requestLifecycleTransaction(StoryTransaction.Reset.INSTANCE);
        }
    }

    public void start() {
        if (disposed || !transactions.isEmpty()) {
            return;
        }
        if (runtime == null || data.isEnded()) {
            player.sendSystemMessage(Component.translatable(ModConstants.Messages.STORY_ALREADY_END).withStyle(ChatFormatting.RED));
            return;
        }

        scheduleTick(0);
    }

    public void choose(int index) {
        stop();

        var runtime = this.runtime;
        if (runtime == null) {
            start();
            return;
        }

        try {
            runtime.choose(index);
            start();
        } catch (Exception ex) {
            handlePlaybackFailure("choose story option", ex);
        } catch (Error error) {
            stopFailedPlayback();
            throw error;
        }
    }

    private void tickStorySafely() {
        try {
            tickStory();
        } catch (Exception ex) {
            handlePlaybackFailure("run story", ex);
        } catch (Error error) {
            stopFailedPlayback();
            throw error;
        }
    }

    private void tickStory() throws Exception {
        if (disposed) {
            cancel();
            cancelTransactionTick();
            return;
        }

        if (consumeTransactions()) {
            return;
        }

        var runtime = this.runtime;
        if (runtime == null || data.isEnded()) {
            player.sendSystemMessage(Component.translatable(ModConstants.Messages.STORY_ALREADY_END).withStyle(ChatFormatting.RED));
            cancel();
            return;
        }

        if (runtime.hasPendingLine()) {
            displayPendingLine();
            finishCurrentLine();
            return;
        }

        if (finishCurrentFlowBoundary()) {
            return;
        }

        var continued = runtime.continueLine();

        if (hasLifecycleTransaction()) {
            return;
        }

        if (consumeTransactions()) {
            return;
        }

        if (!continued) {
            finishCurrentFlowBoundary();
            return;
        }

        displayPendingLine();
        finishCurrentLine();
    }

    private void displayPendingLine() throws Exception {
        var runtime = this.runtime;
        if (runtime == null) {
            return;
        }

        var line = runtime.currentLine();
        if (!line.isBlank()) {
            showLine(line);
        }
        runtime.clearPendingLine();
    }

    private void finishCurrentLine() {
        var runtime = this.runtime;
        if (runtime == null || finishCurrentFlowBoundary()) {
            return;
        }

        var pause = resolvePauseTicks();
        if (pause == -1) {
            showClickToNext();
            cancel();
            return;
        }

        scheduleTick(pause);
    }

    private boolean finishCurrentFlowBoundary() {
        var runtime = this.runtime;
        if (runtime == null) {
            return true;
        }

        if (runtime.hasChoice()) {
            showChoices();
            cancel();
            return true;
        }

        if (runtime.isCurrentFlowEnded()) {
            showStoryEnd();
            data.resetState();
            cancel();
            this.runtime = null;
            return true;
        }

        return false;
    }

    private int resolvePauseTicks() {
        if (data.hasVariable(ModConstants.Variables.LINE_PAUSE_TICKS)) {
            var v = data.getVariable(ModConstants.Variables.LINE_PAUSE_TICKS, IntStoryValue.class);
            if (v != null && v >= -1) {
                return v;
            }
        }

        return ModConstants.Values.DEFAULT_PAUSE_TICKS;
    }

    public void stop(boolean showContinue) {
        if (isStoryRunning()) {
            cancel();
        }

        if (showContinue && !isStoryEnded()) {
            showClickToCurrent();
        }
    }

    public void stop() {
        stop(false);
    }

    public void dispose() {
        disposed = true;
        stop();
        cancelTransactionTick();
        transactions.clear();
        runtime = null;
    }

    public boolean isStoryRunning() {
        return runtime != null && cancellationToken != null && !cancellationToken.isCancelled();
    }

    public boolean isStoryEnded() {
        return runtime == null || data.isEnded();
    }

    private void showLine(String line) {
        player.sendSystemMessage(TextStyleHelper.parseStyle(line.trim(), player.registryAccess()));
    }

    private void showChoices() {
        var runtime = this.runtime;
        if (runtime == null) {
            return;
        }

        var token = UUID.randomUUID();
        data.setContinuousToken(token);
        var choices = runtime.getChoices();
        for (int i = 0; i < choices.size(); i++) {
            var choice = choices.get(i);
            var component = Component.translatable(ModConstants.Messages.STORY_NEXT_CHOICE, TextStyleHelper.parseStyle(choice.getText().trim(), player.registryAccess())).withStyle(ChatFormatting.YELLOW);
            component.setStyle(component.getStyle().withClickEvent(new ClickEvent.RunCommand("/inkraft next " + token + " " + i)));
            component.setStyle(component.getStyle().withHoverEvent(new HoverEvent.ShowText(Component.translatable(ModConstants.Messages.STORY_NEXT_CHOICE_HINT))));
            player.sendSystemMessage(component);
        }
    }

    private void showClickToNext() {
        var token = UUID.randomUUID();
        data.setContinuousToken(token);
        var component = Component.translatable(ModConstants.Messages.STORY_NEXT).withStyle(ChatFormatting.YELLOW);
        component.setStyle(component.getStyle().withClickEvent(new ClickEvent.RunCommand("/inkraft next " + token)));
        component.setStyle(component.getStyle().withHoverEvent(new HoverEvent.ShowText(Component.translatable(ModConstants.Messages.STORY_NEXT_HINT))));
        player.sendSystemMessage(component);
    }

    private void showClickToCurrent() {
        data.setContinuousToken(null);
        var component = Component.translatable(ModConstants.Messages.STORY_NEXT).withStyle(ChatFormatting.YELLOW);
        component.setStyle(component.getStyle().withClickEvent(new ClickEvent.RunCommand("/inkraft current")));
        component.setStyle(component.getStyle().withHoverEvent(new HoverEvent.ShowText(Component.translatable(ModConstants.Messages.STORY_NEXT_HINT))));
        player.sendSystemMessage(component);
    }

    private void showStoryEnd() {
        var component = Component.translatable(ModConstants.Messages.STORY_END).withStyle(ChatFormatting.GREEN);
        player.sendSystemMessage(component);
    }

    // endregion

    // region Internal

    private void requestLifecycleTransaction(StoryTransaction.Lifecycle transaction) {
        stop();
        data.setContinuousToken(null);
        enqueueTransaction(transaction);
    }

    private void enqueueTransaction(StoryTransaction transaction) {
        transactions.addLast(transaction);
        scheduleTransactionTick();
    }

    private boolean hasLifecycleTransaction() {
        for (var transaction : transactions) {
            if (transaction instanceof StoryTransaction.Lifecycle) {
                return true;
            }
        }
        return false;
    }

    private boolean consumeTransactions() throws Exception {
        if (transactions.isEmpty()) {
            return false;
        }

        cancelTransactionTick();
        var batch = drainTransactions();
        if (batch.lifecycle() instanceof StoryTransaction.Replace replace) {
            applyReplaceTransaction(replace);
            return true;
        }
        if (batch.lifecycle() instanceof StoryTransaction.Reset) {
            applyResetTransaction();
            return true;
        }

        var runtime = this.runtime;
        var flowApplied = runtime != null && runtime.applyFlowTransactions(batch.flows());
        if (flowApplied || batch.pauseRequested() && runtime != null && !data.isEnded()) {
            showClickToNext();
            cancel();
            return true;
        }
        return false;
    }

    private TransactionBatch drainTransactions() {
        StoryTransaction.Lifecycle lifecycle = null;
        var flows = new ArrayList<StoryTransaction.Flow>();
        var pauseRequested = false;

        while (!transactions.isEmpty()) {
            switch (transactions.removeFirst()) {
                case StoryTransaction.Lifecycle transaction -> lifecycle = transaction;
                case StoryTransaction.Flow transaction -> flows.add(transaction);
                case StoryTransaction.Pause _ -> pauseRequested = true;
            }
        }

        if (lifecycle != null) {
            return new TransactionBatch(lifecycle, List.of(), false);
        }
        return new TransactionBatch(null, List.copyOf(flows), pauseRequested);
    }

    private void applyReplaceTransaction(StoryTransaction.Replace transaction) {
        stop();
        data.setStory(transaction.storyId());
        data.setState(null);
        data.setEnded(false);
        data.clearPendingLines();
        data.setContinuousToken(null);
        runtime = transaction.runtime();
        start();
    }

    private void applyResetTransaction() {
        stop();
        data.clearData();
        runtime = null;
    }

    private void bindStoryFunctions(Story story) throws Exception {
        var functions = player.registryAccess().lookupOrThrow(InkraftRegistries.STORY_FUNCTIONS);
        for (var entry : functions.entrySet()) {
            var func = entry.getValue();

            story.bindExternalFunction(func.getName(), args -> {
                try {
                    var functionArgs = new IStoryValue<?, ?>[args.length];
                    for (int i = 0; i < args.length; i++) {
                        functionArgs[i] = IStoryValue.fromObject(args[i]);
                    }
                    return func.apply(this, functionArgs).asObject();
                } catch (FunctionSyntaxException ex) {
                    var message = formatStoryFailure(
                            "run Ink function " + func.getName() + " with args " + Arrays.toString(args),
                            data.getStory(), ex);
                    log.warn(message, ex);
                    sendDebugFailure(message);
                    return false;
                }
            }, func.isLookaheadSafe());
        }
    }

    private void handlePlaybackFailure(String operation, Exception ex) {
        stopFailedPlayback();
        reportStoryFailure(operation, data.getStory(), ex);
    }

    private void stopFailedPlayback() {
        cancel();
        cancelTransactionTick();
        transactions.clear();
        data.setContinuousToken(null);
    }

    private void reportStoryFailure(String operation, @Nullable Identifier storyId, Exception ex) {
        var message = formatStoryFailure(operation, storyId, ex);
        log.error(message, ex);
        sendDebugFailure(message);
    }

    private String formatStoryFailure(String operation, @Nullable Identifier storyId, Exception ex) {
        var id = storyId == null ? "<none>" : storyId.toString();
        return "Failed to " + operation
                + " for player " + player.getName().getString() + "(" + player.getUUID() + ")"
                + " in story " + id + ": " + ex;
    }

    private void sendDebugFailure(String message) {
        if (manager.isDebug()) {
            player.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.RED));
        }
    }

    // endregion

    // region Async Cancellation

    private record TransactionBatch(@Nullable StoryTransaction.Lifecycle lifecycle,
                                    List<StoryTransaction.Flow> flows,
                                    boolean pauseRequested) {
    }

    @Nullable
    @Getter(AccessLevel.NONE)
    private CancellableToken cancellationToken;

    @Nullable
    @Getter(AccessLevel.NONE)
    private CancellableToken transactionToken;

    private void scheduleTick(int delay) {
        cancel();
        cancellationToken = new CancellableToken();
        manager.getTaskManager().run(this::tickStorySafely, cancellationToken, delay, -1);
    }

    private void cancel() {
        if (cancellationToken != null) {
            cancellationToken.cancel();
            cancellationToken = null;
        }
    }

    private void scheduleTransactionTick() {
        if (disposed || transactionToken != null && !transactionToken.isCancelled()) {
            return;
        }

        transactionToken = new CancellableToken();
        manager.getTaskManager().run(this::tickStorySafely, transactionToken, 0, -1);
    }

    private void cancelTransactionTick() {
        if (transactionToken != null) {
            transactionToken.cancel();
            transactionToken = null;
        }
    }

    // endregion
}
