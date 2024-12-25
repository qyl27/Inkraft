package cx.rain.mc.inkraft.story;

import com.bladecoder.ink.runtime.Choice;
import com.bladecoder.ink.runtime.Story;
import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.data.story.StoryRegistry;
import cx.rain.mc.inkraft.timer.ITaskManager;
import cx.rain.mc.inkraft.story.function.StoryFunctions;
import cx.rain.mc.inkraft.platform.IInkPlayerData;
import cx.rain.mc.inkraft.timer.cancellation.CancellableToken;
import cx.rain.mc.inkraft.utility.StringArgumentParseHelper;
import cx.rain.mc.inkraft.utility.TextStyleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.util.*;

public class StoryInstance {
    private final Logger logger;
    private final StoriesManager manager;
    private final StoryRegistry registry;
    private final ITaskManager taskManager;

    private final ServerPlayer player;
    private final IInkPlayerData data;

    private Story story;
    private CancellableToken cancellationToken;

    public StoryInstance(Logger logger, StoriesManager manager, StoryRegistry registry, ITaskManager taskManager,
                         ServerPlayer player, IInkPlayerData data) {
        this.logger = logger;
        this.manager = manager;
        this.registry = registry;
        this.taskManager = taskManager;
        this.player = player;
        this.data = data;

        loadStory();
    }

    /// <editor-fold desc="Dependencies.">

    public Logger getLogger() {
        return logger;
    }

    public StoriesManager getManager() {
        return manager;
    }

    public StoryRegistry getRegistry() {
        return registry;
    }

    public ITaskManager getTaskManager() {
        return taskManager;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public IInkPlayerData getData() {
        return data;
    }

    public Story getStory() {
        return story;
    }

    public CancellableToken getCancellationToken() {
        return cancellationToken;
    }

    /// </editor-fold>

    /// <editor-fold desc="Init.">

    public void newStory(ResourceLocation path) {
        stop();
        data.setStory(path);
        data.setEnded(false);

        var str = registry.get(path);
        try {
            story = new Story(str);
            bindStoryFunctions();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void loadStory() {
        if (!data.hasData()) {
            return;
        }

        var storyId = data.getStory();
        if (!registry.has(storyId)) {
            data.resetState();
            logger.warn("Story {} is no longer exists.", storyId);
            return;
        }

        newStory(storyId);

        try {
            story.getState().loadJson(data.getState());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void saveStory() {
        try {
            data.setState(story.getState().toJson());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /// </editor-fold>

    /// <editor-fold desc="Game control.">

    public void start() {
        if (isStoryEnded()) {
            player.sendSystemMessage(Component.translatable(ModConstants.Messages.STORY_ALREADY_END).withStyle(ChatFormatting.RED));
            return;
        }

        int pause = ModConstants.Values.DEFAULT_PAUSE_TICKS;
        if (data.hasVariable(ModConstants.Variables.LINE_PAUSE_TICKS)) {
            var v = data.getVariable(ModConstants.Variables.LINE_PAUSE_TICKS, IStoryVariable.Int.class);
            if (v != null) {
                pause = v;
            }
        }

        if (cancellationToken != null) {
            cancellationToken.cancel();
        }

        data.setEnded(!hasNextLine());

        cancellationToken = new CancellableToken();
        var finalPause = pause;
        taskManager.run(() -> tickStory(finalPause), cancellationToken, 0, pause);
    }

    private void tickStory(final int pause) {
        if (!currentLine().isBlank()) {
            showLine();
        }

        if (hasChoice()) {
            showChoices();
            cancellationToken.cancel();
            return;
        }

        if (pause == -1) {
            showClickToNext();
            cancellationToken.cancel();
            return;
        }

        if (!cancellationToken.isCancelled()) {
            if (hasNextLine()) {
                nextLine();
            } else {
                showStoryEnd();
                data.resetState();
                cancellationToken.cancel();
            }
        }
    }

    public void stop(boolean showContinue) {
        if (isStoryRunning()) {
            cancellationToken.cancel();
        }

        if (showContinue && !isStoryEnded()) {
            showClickToCurrent();
        }
    }

    public void stop() {
        stop(false);
    }

    public boolean isStoryRunning() {
        return story != null && cancellationToken != null && !cancellationToken.isCancelled();
    }

    private void showLine() {
        player.sendSystemMessage(TextStyleHelper.parseStyle(currentLine().trim()));
    }

    private void showChoices() {
        var token = UUID.randomUUID();
        data.setContinuousToken(token);
        var choices = getChoices();
        for (int i = 0; i < choices.size(); i++) {
            var choice = choices.get(i);
            var component = Component.translatable(ModConstants.Messages.STORY_NEXT_CHOICE, TextStyleHelper.parseStyle(choice.getText().trim())).withStyle(ChatFormatting.GREEN);
            component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inkraft next " + token + " " + i)));
            component.setStyle(component.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(ModConstants.Messages.STORY_NEXT_CHOICE_HINT).withStyle(ChatFormatting.YELLOW))));
            player.sendSystemMessage(component);
        }
    }

    private void showClickToNext() {
        var token = UUID.randomUUID();
        data.setContinuousToken(token);
        var component = Component.translatable(ModConstants.Messages.STORY_NEXT).withStyle(ChatFormatting.GREEN);
        component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inkraft next " + token)));
        component.setStyle(component.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(ModConstants.Messages.STORY_NEXT_HINT).withStyle(ChatFormatting.YELLOW))));
        player.sendSystemMessage(component);
    }

    private void showClickToCurrent() {
        var token = UUID.randomUUID();
        data.setContinuousToken(token);
        var component = Component.translatable(ModConstants.Messages.STORY_NEXT).withStyle(ChatFormatting.GREEN);
        component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inkraft current")));
        component.setStyle(component.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(ModConstants.Messages.STORY_NEXT_HINT).withStyle(ChatFormatting.YELLOW))));
        player.sendSystemMessage(component);
    }

    private void showStoryEnd() {
        var component = Component.translatable(ModConstants.Messages.STORY_END).withStyle(ChatFormatting.GREEN);
        player.sendSystemMessage(component);
    }

    /// </editor-fold>

    /// <editor-fold desc="Safe story.">

    public boolean isStoryEnded() {
        return story == null || data.isEnded();
    }

    public String currentLine() {
        try {
            return story.getCurrentText();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean hasNextLine() {
        return story != null && (story.canContinue() || hasChoice());
    }

    public void nextLine() {
        try {
            story.Continue();
            saveStory();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean hasChoice() {
        return story != null && !story.getCurrentChoices().isEmpty();
    }

    public void choose(int index) {
        try {
            story.chooseChoiceIndex(index);
            nextLine();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Choice> getChoices() {
        return story.getCurrentChoices();
    }

    /// </editor-fold>

    /// <editor-fold desc="Parallel flows.">

    public boolean isDefaultFlow() {
        return story.currentFlowIsDefaultFlow();
    }

    public String getFlowName() {
        return story.getCurrentFlowName();
    }

    public void addFlow(String name, String knot) {
        try {
            story.switchFlow(name);
            story.choosePathString(knot);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void removeFlow(String name) {
        try {
            story.removeFlow(name);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void flowTo(String name) {
        try {
            story.switchFlow(name);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void flowBackDefault() {
        try {
            story.switchToDefaultFlow();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<String> getFlows() {
        return story.aliveFlowNames();
    }

    /// </editor-fold>

    /// <editor-fold desc="Internal.">

    private void bindStoryFunctions() {
        try {
            for (var entry : StoryFunctions.FUNCTIONS) {
                var func = entry.get();

                story.bindExternalFunction(func.getName(), args -> {
                    try {
                        var unescaped = Arrays.stream(args)
                                .map(Object::toString)
                                .map(StringArgumentParseHelper::unescape)
                                .toArray(String[]::new);

                        var result = func.apply(this, unescaped);
                        if (result instanceof IStoryVariable.Str(String value)) {
                            return value;
                        } else if (result instanceof IStoryVariable.Int(int value)) {
                            return value;
                        } else if (result instanceof IStoryVariable.Float(float value)) {
                            return value;
                        } else if (result instanceof IStoryVariable.Bool(boolean value)) {
                            return value;
                        }
                        return result;
                    } catch (RuntimeException ex) {
                        logger.error("Running function {}", func.getName(), ex);
                        return IStoryVariable.Bool.FALSE;
                    }
                }, false);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /// </editor-fold>
}
