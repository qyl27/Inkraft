package cx.rain.mc.inkraft.story;

import com.bladecoder.ink.runtime.Choice;
import com.bladecoder.ink.runtime.Story;
import cx.rain.mc.inkraft.Constants;
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

import java.util.*;

public class StoryInstance {
    private final StoriesManager manager;
    private final StoryRegistry registry;
    private final ITaskManager taskManager;

    private final ServerPlayer player;
    private final IInkPlayerData data;

    private Story story;
    private CancellableToken cancellationToken;

    public StoryInstance(StoriesManager manager, StoryRegistry registry, ITaskManager taskManager,
                         ServerPlayer player, IInkPlayerData data) {
        this.manager = manager;
        this.registry = registry;
        this.taskManager = taskManager;
        this.player = player;
        this.data = data;
    }

    /// <editor-fold desc="Dependencies.">

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
        int pause = 20;
        if (data.hasVariable("line_pause_ticks")) {
            var v = data.getVariable("line_pause_ticks", IStoryVariable.Int.class);
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
            var component = Component.translatable(Constants.MESSAGE_STORY_CONTINUE_CHOICE, choice.getText()).withStyle(ChatFormatting.YELLOW);
            component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inkraft next " + token + " " + i)));
            component.setStyle(component.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(Constants.MESSAGE_STORY_HINT_CONTINUE_CHOICE).withStyle(ChatFormatting.GREEN))));
            player.sendSystemMessage(component);
        }
    }

    private void showClickToNext() {
        var token = UUID.randomUUID();
        data.setContinuousToken(token);
        var component = Component.translatable(Constants.MESSAGE_STORY_CONTINUE).withStyle(ChatFormatting.YELLOW);
        component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inkraft continue " + token)));
        component.setStyle(component.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(Constants.MESSAGE_STORY_HINT_CONTINUE).withStyle(ChatFormatting.GREEN))));
        player.sendSystemMessage(component);
    }

    /// </editor-fold>

    /// <editor-fold desc="Safe story.">

    private boolean isStoryEnded() {
        return story == null || (!story.canContinue() && !hasChoice());
    }

    private String currentLine() {
        try {
            return story.getCurrentText();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private boolean hasNextLine() {
        return story != null && (story.canContinue() || hasChoice());
    }

    private void nextLine() {
        try {
            var message = story.Continue().trim();
            while (message.isBlank()) {
                message = story.Continue().trim();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private boolean hasChoice() {
        return story != null && !story.getCurrentChoices().isEmpty();
    }

    private List<Choice> getChoices() {
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
                }, false);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /// </editor-fold>
}
