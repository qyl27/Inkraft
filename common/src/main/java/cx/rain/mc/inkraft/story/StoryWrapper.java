package cx.rain.mc.inkraft.story;

import com.bladecoder.ink.runtime.Story;
import cx.rain.mc.inkraft.command.CommandConstants;
import cx.rain.mc.inkraft.data.StoriesManager;
import cx.rain.mc.inkraft.story.function.StoryFunctionResults;
import cx.rain.mc.inkraft.story.function.StoryFunctionsRegistry;
import cx.rain.mc.inkraft.utility.InkTagCommandHelper;
import cx.rain.mc.inkraft.utility.TextStyleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class StoryWrapper {

    private final StoriesManager manager;

    private Story story;

    public StoryWrapper(StoriesManager manager) {
        this.manager = manager;
    }

    public boolean startStory(ServerPlayer player, IInkStoryStateHolder holder, ResourceLocation path) {
        flowTo(path);
        bindStoryFunctions(story, player);
        return continueStory(player, holder);
    }

    private boolean continueStory(ServerPlayer player, IInkStoryStateHolder holder) {
        try {
            if (story.canContinue()) {
                var message = story.Continue().trim();

                player.sendSystemMessage(TextStyleHelper.parseStyle(message));

                var tags = story.getCurrentTags();
                var ops = InkTagCommandHelper.parseTag(tags);
                InkTagCommandHelper.runTagCommands(ops, player);

                var choices = story.getCurrentChoices();

                var token = UUID.randomUUID();
                holder.setContinueToken(token);

                if (choices.size() == 0) {
                    if (story.canContinue()) {
                        var component = Component.translatable(CommandConstants.MESSAGE_STORY_CONTINUE).withStyle(ChatFormatting.YELLOW);
                        component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inkraft continue " + token)));
                        component.setStyle(component.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(CommandConstants.MESSAGE_STORY_HINT_CONTINUE).withStyle(ChatFormatting.GREEN))));
                        player.sendSystemMessage(component);
                    }
                } else {
                    for (int i = 0; i < choices.size(); i++) {
                        var choice = choices.get(i);
                        var component = Component.translatable(CommandConstants.MESSAGE_STORY_CONTINUE_CHOICE, choice.getText()).withStyle(ChatFormatting.YELLOW);
                        component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inkraft continue " + token + " " + i)));
                        component.setStyle(component.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(CommandConstants.MESSAGE_STORY_HINT_CONTINUE_CHOICE).withStyle(ChatFormatting.GREEN))));
                        player.sendSystemMessage(component);
                    }
                }

                save(holder);
                return true;
            } else {
                save(holder);
                return false;
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

        return continueStory(player, holder);
    }

    public boolean continueStoryWithChoice(ServerPlayer player, IInkStoryStateHolder holder, int choice) {
        try {
            load(holder);
            story.chooseChoiceIndex(choice);
            save(holder);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return continueStory(player, holder);
    }

    public void save(IInkStoryStateHolder stateHolder) {
        try {
            stateHolder.setState(story.getState().toJson());
        } catch (Exception ex) {
            ex.printStackTrace();
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
//            if (story.currentFlowIsDefaultFlow()) {
//                story.switchFlow(path.toString());
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindStoryFunctions(Story story, ServerPlayer player) {
        try {
            for (var funcSupplier : StoryFunctionsRegistry.FUNCTIONS) {
                var func = funcSupplier.get();
                var funcName = func.getName().isBlank() ? funcSupplier.getRegistryId().getPath() : func.getName();

                story.bindExternalFunction(funcName, args -> {
                    var result = func.func().apply(args, player);
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
}
