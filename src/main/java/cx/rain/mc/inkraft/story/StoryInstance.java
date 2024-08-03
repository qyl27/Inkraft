package cx.rain.mc.inkraft.story;

import com.bladecoder.ink.runtime.Choice;
import com.bladecoder.ink.runtime.Story;
import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.data.story.StoryRegistry;
import cx.rain.mc.inkraft.timer.ITaskManager;
import cx.rain.mc.inkraft.story.function.StoryFunctions;
import cx.rain.mc.inkraft.platform.IInkPlayerData;
import cx.rain.mc.inkraft.timer.cancellation.CancellableToken;
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
        data.setStory(path);
        data.resetState();
        var str = registry.get(path);
        try {
            story = new Story(str);
            bindStoryFunctions();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void loadStory() {
        newStory(data.getStory());
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
        int pause = ModConstants.Values.DEFAULT_PAUSE_TICKS;
        if (data.hasVariable(ModConstants.Variables.LINE_PAUSE_TICKS)) {
            var v = data.getVariable(ModConstants.Variables.LINE_PAUSE_TICKS, IStoryVariable.Int.class);
            if (v != null) {
                pause = v;
            }
        }

        cancellationToken = new CancellableToken();
        var finalPause = pause;
        taskManager.run(() -> {
            showLine();
            if (hasChoice()) {
                showChoices();
                cancellationToken.cancel();
                return;
            }

            if (finalPause == -1) {
                showClickToNext();
                cancellationToken.cancel();
                return;
            }

            if (hasNextLine()) {
                nextLine();
            }
        }, cancellationToken, 0, pause);
    }

    private void showLine() {
        player.sendSystemMessage(TextStyleHelper.parseStyle(currentLine()));
    }

    private void showChoices() {
        var token = UUID.randomUUID();
        data.setContinuousToken(token);
        var choices = getChoices();
        for (int i = 0; i < choices.size(); i++) {
            var choice = choices.get(i);
            var component = Component.translatable(ModConstants.MESSAGE_STORY_CONTINUE_CHOICE, choice.getText()).withStyle(ChatFormatting.YELLOW);
            component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inkraft next " + token + " " + i)));
            component.setStyle(component.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(ModConstants.MESSAGE_STORY_HINT_CONTINUE_CHOICE).withStyle(ChatFormatting.GREEN))));
            player.sendSystemMessage(component);
        }
    }

    private void showClickToNext() {
        var token = UUID.randomUUID();
        data.setContinuousToken(token);
        var component = Component.translatable(ModConstants.MESSAGE_STORY_CONTINUE).withStyle(ChatFormatting.YELLOW);
        component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inkraft continue " + token)));
        component.setStyle(component.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(ModConstants.MESSAGE_STORY_HINT_CONTINUE).withStyle(ChatFormatting.GREEN))));
        player.sendSystemMessage(component);
    }

    /// </editor-fold>

    /// <editor-fold desc="Safe story.">

    public boolean isStoryEnded() {
        return story == null || (!story.canContinue() && !hasChoice());
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
            var message = story.Continue().trim();
            while (message.isBlank()) {
                message = story.Continue().trim();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean hasChoice() {
        return story != null && !story.getCurrentChoices().isEmpty();
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
                        var result = func.apply(this, args);
                        if (result instanceof IStoryVariable.Str strVar) {
                            return strVar.value();
                        } else if (result instanceof IStoryVariable.Int intVar) {
                            return intVar.value();
                        } else if (result instanceof IStoryVariable.Float doubleVar) {
                            return doubleVar.value();
                        } else if (result instanceof IStoryVariable.Bool boolVar) {
                            return boolVar.value();
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
