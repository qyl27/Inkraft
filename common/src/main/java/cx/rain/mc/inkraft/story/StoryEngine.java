package cx.rain.mc.inkraft.story;

import com.bladecoder.ink.runtime.Story;
import com.bladecoder.ink.runtime.VariablesState;
import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.Constants;
import cx.rain.mc.inkraft.data.StoriesManager;
import cx.rain.mc.inkraft.networking.packet.S2CHideAllVariablePacket;
import cx.rain.mc.inkraft.story.function.StoryFunctionResults;
import cx.rain.mc.inkraft.story.function.StoryFunctions;
import cx.rain.mc.inkraft.story.state.IInkStoryStateHolder;
import cx.rain.mc.inkraft.utility.InkTagCommandHelper;
import cx.rain.mc.inkraft.utility.TextStyleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class StoryEngine {

    private final StoriesManager manager;

    private Story story;
    private ServerPlayer player;
    private IInkStoryStateHolder holder;

    public StoryEngine(StoriesManager manager, ServerPlayer player, IInkStoryStateHolder holder) {
        this.manager = manager;
        this.player = player;
        this.holder = holder;
    }

    public boolean startStory(ResourceLocation path, boolean debug) {
        flowTo(path);
        bindStoryFunctions(debug);
        return continueStory(new AsyncToken());
    }

    // XXX: why?
//    private String cachedContinue = "";

    private boolean continueStory(AsyncToken asyncToken) {
        try {
            if (story.canContinue()) {
                var message = story.Continue().trim();

//                if (message.equals(cachedContinue)) {
//                    message = story.Continue().trim();
//                }
//
//                cachedContinue = message;

                var tags = story.getCurrentTags();
                var ops = InkTagCommandHelper.parseTag(tags);
                InkTagCommandHelper.runTagCommands(this, ops, player);

                player.sendSystemMessage(TextStyleHelper.parseStyle(message));

                var choices = story.getCurrentChoices();

                var token = UUID.randomUUID();
                holder.setContinueToken(token);

                if (choices.size() == 0) {
                    if (story.canContinue()) {
                        if (autoContinue) {
                            if (!asyncToken.isAsync()) {
                                Inkraft.getInstance().getTimerManager().addTimer(player, () -> {
                                    if (asyncToken.isCanceled()) {
                                        Inkraft.getInstance().getTimerManager().removeTimers(player);
                                        return;
                                    }

                                    var story = Inkraft.getInstance().getStoriesManager().getStory(player);
                                    var result = story.continueStory(asyncToken.async());
                                    if (!result || !story.canAutoContinue()) {
                                        asyncToken.cancel();
                                    }
                                }, 0, continueSpeed);
                            }

                            save(false);
                            return true;
                        }

                        var component = Component.translatable(Constants.MESSAGE_STORY_CONTINUE).withStyle(ChatFormatting.YELLOW);
                        component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inkraft continue " + token)));
                        component.setStyle(component.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(Constants.MESSAGE_STORY_HINT_CONTINUE).withStyle(ChatFormatting.GREEN))));
                        player.sendSystemMessage(component);

                        save(false);
                        return true;
                    } else {
                        save(true);
                        return true;
                    }
                } else {
                    for (int i = 0; i < choices.size(); i++) {
                        var choice = choices.get(i);
                        var component = Component.translatable(Constants.MESSAGE_STORY_CONTINUE_CHOICE, choice.getText()).withStyle(ChatFormatting.YELLOW);
                        component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inkraft continue " + token + " " + i)));
                        component.setStyle(component.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(Constants.MESSAGE_STORY_HINT_CONTINUE_CHOICE).withStyle(ChatFormatting.GREEN))));
                        player.sendSystemMessage(component);
                    }

                    save(false);
                    return true;
                }
            } else {
                if (story.getCurrentChoices().size() == 0) {
                    save(true);
                } else {
                    save(false);
                }

                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean continueStoryWithoutChoice() {
        try {
            load();
        } catch (RuntimeException ignored) {
            // Silent is gold.
        }

        return continueStory(new AsyncToken());
    }

    public boolean continueStoryWithChoice(int choice) {
        try {
            load();
            story.chooseChoiceIndex(choice);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return continueStory(new AsyncToken());
    }

    public void save(boolean isStoryEnd) {
        try {
            holder.setState(story.getState().toJson());
            holder.setInStory(!isStoryEnd);
        } catch (Exception ex) {
            // qyl27: silent is gold.
        }
    }

    public void load() {
        try {
            story.getState().loadJson(holder.getState());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void flowTo(ResourceLocation path) {
        try {
            // Todo: qyl27: flow support!
            story = new Story(manager.getStoryString(path));

            clean();
//            if (story.currentFlowIsDefaultFlow()) {
//                story.switchFlow(path.toString());
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void clean() {
        autoContinue = false;
        continueSpeed = -1;
        variableObservers.clear();

        Inkraft.getInstance().getNetworking().sendToPlayer(player, new S2CHideAllVariablePacket());
    }

    private void bindStoryFunctions(boolean debug) {
        try {
            for (var funcSupplier : StoryFunctions.FUNCTIONS) {
                var func = funcSupplier.get();
                var funcName = func.getName().isBlank() ? funcSupplier.getRegistryId().getPath() : func.getName();

                story.bindExternalFunction(funcName, args -> {
                    var result = func.func(debug).apply(args, player);
                    if (result instanceof StoryFunctionResults.StringResult stringResult) {
                        return stringResult.stringResult();
                    } else if (result instanceof StoryFunctionResults.IntResult intResult) {
                        return intResult.intResult();
                    } else if (result instanceof StoryFunctionResults.BoolResult boolResult) {
                        return boolResult.boolResult();
                    }
                    return result;
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean canAutoContinue() {
        return story.canContinue();
    }

    private boolean autoContinue = false;

    private long continueSpeed = -1;

    public void setAutoContinue(boolean value) {
        autoContinue = value;

        if (autoContinue && continueSpeed <= 0) {
            continueSpeed = 20;
        }

        if (!autoContinue) {
            Inkraft.getInstance().getTimerManager().removeTimers(player);
        }
    }

    public void setContinueSpeed(long value) {
        if (value > 0) {
            continueSpeed = value;
        }
    }

    private static class AsyncToken {
        private boolean isCanceled = false;
        private boolean isAsync = false;

        public boolean isCanceled() {
            return isCanceled;
        }

        public boolean isAsync() {
            return isAsync;
        }

        public AsyncToken async() {
            isAsync = true;
            return this;
        }

        public AsyncToken cancel() {
            isCanceled = true;
            return this;
        }
    }

    public VariablesState getVariablesState() {
        return story.getVariablesState();
    }

    private final Map<String, List<Story.VariableObserver>> variableObservers = new HashMap<>();

    public void observerVariable(String name, Story.VariableObserver observer) {
        if (!variableObservers.containsKey(name)) {
            variableObservers.put(name, new ArrayList<>());
        }

        try {
            story.observeVariable(name, observer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        variableObservers.get(name).add(observer);
    }

    public void removeVariableObserver(String name) {
        try {
            var list = variableObservers.get(name);
            if (list != null) {
                for (var observer : list) {
                    story.removeVariableObserver(observer, name);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
