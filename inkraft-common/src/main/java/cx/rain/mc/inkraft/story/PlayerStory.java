package cx.rain.mc.inkraft.story;

import com.bladecoder.ink.runtime.Story;
import cx.rain.mc.inkraft.mod.InkraftMod;
import cx.rain.mc.inkraft.Constants;
import cx.rain.mc.inkraft.story.function.StoryFunctions;
import cx.rain.mc.inkraft.mod.utility.TextStyleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class PlayerStory {
    private final Story story;
    private final ServerPlayer player;
    private boolean debug = false;

    public PlayerStory(ServerPlayer player, Story story) {
        this(player, story, true);
    }

    public PlayerStory(ServerPlayer player, Story story, boolean debug) {
        this.story = story;
        this.player = player;
        this.debug = debug;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public boolean isDebug() {
        return debug;
    }

    //    public boolean startStory(ResourceLocation path, boolean debug) {
//        this.debug = debug;
//
//        flowTo(path);
//        bindStoryFunctions();
//        return continueStory(new AsyncToken());
//    }
//
//    public boolean isDebug() {
//        return debug;
//    }

    private boolean continueStory(AsyncToken asyncToken) {
        try {
            if (story.canContinue()) {
                var message = story.Continue().trim();

                while (message.isEmpty()) {
                    message = story.Continue().trim();
                }

                player.sendSystemMessage(TextStyleHelper.parseStyle(message));

                var choices = story.getCurrentChoices();
                if (choices.isEmpty()) {
                    if (story.canContinue()) {
                        if (canAutoContinue()) {
                            if (!asyncToken.isAsync()) {
                                InkraftMod.getInstance().getTimerManager().addTimer(player, () -> {
                                    if (asyncToken.isCanceled()) {
                                        InkraftMod.getInstance().getTimerManager().removeTimers(player);
                                        return;
                                    }

                                    var story = InkraftMod.getInstance().getStoriesManager().getStory(player);

                                    if (story.canAutoContinue()) {
                                        var result = story.continueStory(asyncToken.async());

                                        if (!result || !story.canAutoContinue()) {
                                            asyncToken.cancel();
                                        }
                                    } else {
                                        sendContinue();
                                    }
                                }, 0, continueSpeed);
                            }
                        } else {
                            sendContinue();
                        }
                    } else {
                        save(message, true);
                        return true;
                    }
                } else {
                    sendContinue();
                }

                save(message, false);
            } else {
                save(null, story.getCurrentChoices().isEmpty());
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void sendContinue() {
        if (!story.canContinue() && story.getCurrentChoices().isEmpty()) {
            return;
        }

        if (hideContinue) {
            return;
        }

        var token = UUID.randomUUID();
        holder.setContinueToken(token);

        if (story.getCurrentChoices().isEmpty()) {
            var component = Component.translatable(Constants.MESSAGE_STORY_CONTINUE).withStyle(ChatFormatting.YELLOW);
            component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inkraft continue " + token)));
            component.setStyle(component.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(Constants.MESSAGE_STORY_HINT_CONTINUE).withStyle(ChatFormatting.GREEN))));
            player.sendSystemMessage(component);
        } else {
            var choices = story.getCurrentChoices();

            for (int i = 0; i < choices.size(); i++) {
                var choice = choices.get(i);
                var component = Component.translatable(Constants.MESSAGE_STORY_CONTINUE_CHOICE, choice.getText()).withStyle(ChatFormatting.YELLOW);
                component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inkraft continue " + token + " " + i)));
                component.setStyle(component.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(Constants.MESSAGE_STORY_HINT_CONTINUE_CHOICE).withStyle(ChatFormatting.GREEN))));
                player.sendSystemMessage(component);
            }
        }
    }

    public void repeatContinue() {
        if (!holder.isInStory()) {
            return;
        }

        var message = holder.getLastMessage();

        autoContinue = holder.getCurrentAutoContinue();
        continueSpeed = holder.getCurrentAutoContinueSpeed();

        player.sendSystemMessage(TextStyleHelper.parseStyle(message));
        sendContinue();
    }

    public boolean continueStoryWithoutChoice() {
        return continueStory(new AsyncToken());
    }

    public boolean continueStoryWithChoice(int choice) {
        try {
            if (story.getCurrentChoices().isEmpty()) {
                story.Continue();
            } else {
                story.chooseChoiceIndex(choice);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return continueStory(new AsyncToken());
    }

//    public void save(String lastMessage, boolean isStoryEnd) {
//        try {
//            if (lastMessage != null) {
//                holder.setLastMessage(lastMessage);
//            }
//
//            holder.setState(story.getState().toJson());
//            holder.setInStory(!isStoryEnd);
//
//            holder.setCurrentAutoContinue(autoContinue);
//            holder.setCurrentAutoContinueSpeed(continueSpeed);
//        } catch (Exception ex) {
//            // qyl27: silent is gold.
//        }
//    }

//    public void load(ResourceLocation path) {
//        try {
//            story = new Story(manager.getStoryString(path));
//            bindStoryFunctions();
//            story.getState().loadJson(holder.getState());
//        } catch (Exception ex) {
//            throw new RuntimeException(ex);
//        }
//    }

//    public void flowTo(ResourceLocation path) {
//        try {
//            // Todo: qyl27: flow support!
//            story = new Story(manager.getStoryString(path));
//            holder.setCurrentStory(path);
//
//            clean();
////            if (story.currentFlowIsDefaultFlow()) {
////                story.switchFlow(path.toString());
////            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

//    public void clean() {
//        autoContinue = false;
//        continueSpeed = -1;
//        variableObservers.clear();
//
//        Inkraft.getInstance().getNetworking().sendToPlayer(player, new S2CHideAllVariablePacket());
//    }

    private void bindStoryFunctions() {
        try {
            for (var funcSupplier : StoryFunctions.FUNCTIONS) {
                var func = funcSupplier.get();
                var funcName = func.getName().isBlank() ? funcSupplier.getRegistryId().getPath() : func.getName();

                story.bindExternalFunction(funcName, args -> {
                    var result = func.func(this).apply(args);
                    if (result instanceof StoryVariables.StrVar strVar) {
                        return strVar.stringValue();
                    } else if (result instanceof StoryVariables.IntVar intVar) {
                        return intVar.intValue();
                    } else if (result instanceof StoryVariables.DoubleVar doubleVar) {
                        return doubleVar.doubleValue();
                    } else if (result instanceof StoryVariables.BoolVar boolVar) {
                        return boolVar.boolValue();
                    }
                    return result;
                }, false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    public boolean canAutoContinue() {
//        return autoContinue && story.canContinue();
//    }
//
//    private boolean autoContinue = false;
//    private long continueSpeed = -1;
//
//    public void setAutoContinue(boolean value) {
//        autoContinue = value;
//
//        if (autoContinue && continueSpeed <= 0) {
//            continueSpeed = 20;
//        }
//
//        if (!autoContinue) {
//            Inkraft.getInstance().getTimerManager().removeTimers(player);
//        }
//    }
//
//    public void setContinueSpeed(long value) {
//        if (value > 0) {
//            continueSpeed = value;
//        }
//    }
//
//    private boolean hideContinue = false;
//
//    public void setHideContinue(boolean isHide) {
//        hideContinue = isHide;
//    }
//
//    private static class AsyncToken {
//        private boolean isCanceled = false;
//        private boolean isAsync = false;
//
//        public boolean isCanceled() {
//            return isCanceled;
//        }
//
//        public boolean isAsync() {
//            return isAsync;
//        }
//
//        public AsyncToken async() {
//            isAsync = true;
//            return this;
//        }
//
//        public AsyncToken cancel() {
//            isCanceled = true;
//            return this;
//        }
//    }
//
//    public VariablesState getVariablesState() {
//        return story.getVariablesState();
//    }
//
//    private final Map<String, List<Story.VariableObserver>> variableObservers = new HashMap<>();
//
//    public void observerVariable(String name, Story.VariableObserver observer) {
//        if (!variableObservers.containsKey(name)) {
//            variableObservers.put(name, new ArrayList<>());
//        }
//
//        try {
//            story.observeVariable(name, observer);
//        } catch (Exception ex) {
//            throw new RuntimeException(ex);
//        }
//
//        variableObservers.get(name).add(observer);
//    }
//
//    public void removeVariableObserver(String name) {
//        try {
//            var list = variableObservers.get(name);
//            if (list != null) {
//                for (var observer : list) {
//                    story.removeVariableObserver(observer, name);
//                }
//            }
//        } catch (Exception ex) {
//            throw new RuntimeException(ex);
//        }
//    }
}
