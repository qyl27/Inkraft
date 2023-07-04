package cx.rain.mc.inkraft.story;

import com.bladecoder.ink.runtime.Story;
import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.command.CommandConstants;
import cx.rain.mc.inkraft.data.StoriesManager;
import cx.rain.mc.inkraft.story.function.StoryFunctionResults;
import cx.rain.mc.inkraft.story.function.StoryFunctions;
import cx.rain.mc.inkraft.utility.InkTagCommandHelper;
import cx.rain.mc.inkraft.utility.TextStyleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class StoryEngine {

    private final StoriesManager manager;

    private Story story;

    public StoryEngine(StoriesManager manager) {
        this.manager = manager;
    }

    public boolean startStory(ServerPlayer player, IInkStoryStateHolder holder, ResourceLocation path, boolean debug) {
        flowTo(path);
        bindStoryFunctions(story, player, debug);
        return continueStory(player, holder, new AsyncToken());
    }

    private boolean continueStory(ServerPlayer player, IInkStoryStateHolder holder, AsyncToken asyncToken) {
        try {
            if (story.canContinue()) {
                var message = story.Continue().trim();

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
                                    var storyHolder = InkraftPlatform.getPlayerStoryStateHolder(player);
                                    var result = story.continueStory(player, storyHolder, asyncToken.async());
                                    if (!result || !story.canAutoContinue()) {
                                        asyncToken.cancel();
                                    }
                                }, 0, continueSpeed);
                            }

                            save(holder, false);
                            return true;
                        }

                        var component = Component.translatable(CommandConstants.MESSAGE_STORY_CONTINUE).withStyle(ChatFormatting.YELLOW);
                        component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inkraft continue " + token)));
                        component.setStyle(component.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(CommandConstants.MESSAGE_STORY_HINT_CONTINUE).withStyle(ChatFormatting.GREEN))));
                        player.sendSystemMessage(component);

                        save(holder, false);
                        return true;
                    } else {
                        save(holder, true);
                        return true;
                    }
                } else {
                    for (int i = 0; i < choices.size(); i++) {
                        var choice = choices.get(i);
                        var component = Component.translatable(CommandConstants.MESSAGE_STORY_CONTINUE_CHOICE, choice.getText()).withStyle(ChatFormatting.YELLOW);
                        component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inkraft continue " + token + " " + i)));
                        component.setStyle(component.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(CommandConstants.MESSAGE_STORY_HINT_CONTINUE_CHOICE).withStyle(ChatFormatting.GREEN))));
                        player.sendSystemMessage(component);
                    }

                    save(holder, false);
                    return true;
                }
            } else {
                if (story.getCurrentChoices().size() == 0) {
                    save(holder, true);
                } else {
                    save(holder, false);
                }

                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean continueStoryWithoutChoice(ServerPlayer player, IInkStoryStateHolder holder) {
        try {
            load(holder);
        } catch (RuntimeException ignored) {
            // Silent is gold.
        }

        return continueStory(player, holder, new AsyncToken());
    }

    public boolean continueStoryWithChoice(ServerPlayer player, IInkStoryStateHolder holder, int choice) {
        try {
            load(holder);
            story.chooseChoiceIndex(choice);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return continueStory(player, holder, new AsyncToken());
    }

    public void save(IInkStoryStateHolder stateHolder, boolean isStoryEnd) {
        try {
            stateHolder.setState(story.getState().toJson());
            stateHolder.setInStory(!isStoryEnd);
        } catch (Exception ex) {
//            ex.printStackTrace();
            // qyl27: silent is gold.
        }
    }

    public void load(IInkStoryStateHolder stateHolder) {
        try {
            story.getState().loadJson(stateHolder.getState());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void flowTo(ResourceLocation path) {
        try {
            // Todo: qyl27: flow support!
            story = new Story(manager.getStoryString(path));

            autoContinue = false;
            continueSpeed = -1;
//            if (story.currentFlowIsDefaultFlow()) {
//                story.switchFlow(path.toString());
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindStoryFunctions(Story story, ServerPlayer player, boolean debug) {
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

    public void setAutoContinue(ServerPlayer player, boolean value) {
        autoContinue = value;

        if (autoContinue && continueSpeed <= 0) {
            continueSpeed = 20;
        }

        if (!value) {
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
}
