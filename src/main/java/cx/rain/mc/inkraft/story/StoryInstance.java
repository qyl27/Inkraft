package cx.rain.mc.inkraft.story;

import com.bladecoder.ink.runtime.Choice;
import com.bladecoder.ink.runtime.Story;
import com.bladecoder.ink.runtime.StoryState;
import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.engine.EngineManager;
import cx.rain.mc.inkraft.registry.InkraftRegistries;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.api.platform.storage.IInkPlayerData;
import cx.rain.mc.inkraft.timer.cancellation.CancellableToken;
import cx.rain.mc.inkraft.utility.TextStyleHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Slf4j
@Getter
public class StoryInstance {
    private final EngineManager manager;

    private final ServerPlayer player;
    private final IInkPlayerData data;

    public StoryInstance(EngineManager manager, ServerPlayer player, IInkPlayerData data) {
        this.manager = manager;
        this.player = player;
        this.data = data;

        loadStory();
    }

    // region Dependencies

    // endregion

    // region Init

    public void newStory(Identifier path) {
        stop();
        data.setStory(path);
        data.setEnded(false);

        try {
            var registry = EngineManager.getInstance().getStoryRegistry();
            if (!registry.has(path)) {
                player.sendSystemMessage(Component.translatable(ModConstants.Messages.STORY_NOT_FOUND, path.toString()).withStyle(ChatFormatting.RED));
                return;
            }
            var str = registry.get(path);
            story = new Story(str);
            story.onError = StoryErrorHandler.INSTANCE;
            bindStoryFunctions();
        } catch (Exception ex) {
            log.error("Error starting story", ex);
        }
    }

    public void loadStory() {
        if (!data.hasData()) {
            return;
        }

        var storyId = data.getStory();
        if (storyId == null) {
            data.resetState();
            return;
        }

        if (!EngineManager.getInstance().getStoryRegistry().has(storyId)) {
            data.resetState();
            log.warn("Story {} is no longer exists.", storyId);
            player.sendSystemMessage(Component.translatable(ModConstants.Messages.STORY_NOT_FOUND, storyId.toString()).withStyle(ChatFormatting.RED));
            return;
        }

        newStory(storyId);

        if (story == null) {
            log.warn("No story to load.");
            return;
        }

        try {
            story.getState().loadJson(data.getState());
        } catch (Exception ex) {
            log.warn("Failed to load state", ex);
        }
    }

    public void saveStory() {
        if (story == null) {
            log.warn("No story to save.");
            return;
        }
        try {
            var state = story.getState().toJson();
            data.setState(state);
        } catch (Exception ex) {
            log.warn("Failed to save state", ex);
        }
    }

    // endregion

    // region Game control

    public void start() {
        if (isStoryEnded()) {
            player.sendSystemMessage(Component.translatable(ModConstants.Messages.STORY_ALREADY_END).withStyle(ChatFormatting.RED));
            return;
        }

        int pause = ModConstants.Values.DEFAULT_PAUSE_TICKS;
        if (data.hasVariable(ModConstants.Variables.LINE_PAUSE_TICKS)) {
            var v = data.getVariable(ModConstants.Variables.LINE_PAUSE_TICKS, IntStoryValue.class);
            if (v != null) {
                pause = v;
            }
        }

        cancel();

        data.setEnded(!hasNextLine());

        cancellationToken = new CancellableToken();
        var finalPause = pause;
        manager.getTaskManager().run(() -> tickStory(finalPause), cancellationToken, 0, pause);
    }

    private void tickStory(final int pause) {
        if (!currentLine().isBlank()) {
            showLine();
        }

        if (hasChoice()) {
            showChoices();
            cancel();
            return;
        }

        if (pause <= -1) {
            showClickToNext();
            cancel();
            return;
        }

        if (!isCancelled()) {
            if (hasNextLine()) {
                nextLine();
            } else {
                showStoryEnd();
                data.resetState();
                cancel();
            }
        }
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

    public boolean isStoryRunning() {
        return story != null && !isCancelled();
    }

    private void showLine() {
        player.sendSystemMessage(TextStyleHelper.parseStyle(currentLine().trim(), player.registryAccess()));
    }

    private void showChoices() {
        var token = UUID.randomUUID();
        data.setContinuousToken(token);
        var choices = getChoices();
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
        var token = UUID.randomUUID();
        data.setContinuousToken(token);
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

    // region Safe story

    @Nullable
    private Story story;

    private boolean hasStory() {
        return story != null;
    }

    public boolean isStoryEnded() {
        return !hasStory() || data.isEnded();
    }

    public String currentLine() {
        if (!hasStory()) {
            log.warn("currentLine: Story ended. It shouldn't happen!");
            return "";
        }

        try {
            return story.getCurrentText();
        } catch (Throwable ex) {
            log.error("An error I can't handle!", ex);
            return "";
        }
    }

    public boolean hasNextLine() {
        return story != null && (story.canContinue() || hasChoice());
    }

    public void nextLine() {
        if (!hasStory()) {
            log.warn("nextLine: Story ended. It shouldn't happen!");
            return;
        }

        try {
            story.Continue();
            saveStory();
        } catch (Throwable ex) {
            log.error("An error I can't handle!", ex);
        }
    }

    public boolean hasChoice() {
        return story != null && !story.getCurrentChoices().isEmpty();
    }

    public void choose(int index) {
        if (!hasStory()) {
            log.warn("choose: Story ended. It shouldn't happen!");
            return;
        }

        try {
            story.chooseChoiceIndex(index);
            nextLine();
        } catch (Throwable ex) {
            log.error("An error I can't handle!", ex);
        }
    }

    public List<Choice> getChoices() {
        if (!hasStory()) {
            log.warn("getChoices: Story ended. It shouldn't happen!");
            return List.of();
        }

        return story.getCurrentChoices();
    }

    // endregion

    // region Parallel flows.">

    public boolean isDefaultFlow() {
        if (!hasStory()) {
            log.warn("isDefaultFlow: Story ended. It shouldn't happen!");
            return false;
        }

        return story.currentFlowIsDefaultFlow();
    }

    public String getFlowName() {
        if (!hasStory()) {
            log.warn("getFlowName: Story ended. It shouldn't happen!");
            return StoryState.kDefaultFlowName;
        }

        return story.getCurrentFlowName();
    }

    public void addFlow(String name, String knot) {
        if (!hasStory()) {
            log.warn("addFlow: Story ended. It shouldn't happen!");
            return;
        }

        try {
            story.switchFlow(name);
            story.choosePathString(knot);
        } catch (Throwable ex) {
            log.error("An error I can't handle!", ex);
        }
    }

    public void removeFlow(String name) {
        if (!hasStory()) {
            log.warn("removeFlow: Story ended. It shouldn't happen!");
            return;
        }

        try {
            story.removeFlow(name);
        } catch (Throwable ex) {
            log.error("An error I can't handle!", ex);
        }
    }

    public void flowTo(String name) {
        if (!hasStory()) {
            log.warn("flowTo: Story ended. It shouldn't happen!");
            return;
        }

        try {
            story.switchFlow(name);
        } catch (Throwable ex) {
            log.error("An error I can't handle!", ex);
        }
    }

    public void flowBackDefault() {
        if (!hasStory()) {
            log.warn("flowBackDefault: Story ended. It shouldn't happen!");
            return;
        }

        try {
            story.switchToDefaultFlow();
        } catch (Throwable ex) {
            log.error("An error I can't handle!", ex);
        }
    }

    public List<String> getFlows() {
        if (!hasStory()) {
            log.warn("getFlows: Story ended. It shouldn't happen!");
            return List.of();
        }

        return story.aliveFlowNames();
    }

    // endregion

    // region Internal

    private void bindStoryFunctions() {
        assert story != null;
        try {
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
                    } catch (Throwable ex) {
                        log.warn("Running function {}", func.getName());
                        for (int i = 0; i < args.length; i++) {
                            var a = args[i];
                            log.warn("Arg {}: {}", i, a);
                        }
                        log.warn("Inner: ", ex);
                    }
                    return false;
                }, func.isLookaheadSafe());
            }
        } catch (Throwable ex) {
            log.error("An error I can't handle!", ex);
        }
    }

    // endregion

    // region Async Cancellation

    @Nullable
    private CancellableToken cancellationToken;

    private void cancel() {
        if (cancellationToken != null) {
            cancellationToken.cancel();
        }
    }

    private boolean isCancelled() {
        return cancellationToken != null && cancellationToken.isCancelled();
    }

    // endregion
}
