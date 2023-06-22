package cx.rain.mc.inkraft.story;

import com.bladecoder.ink.runtime.Story;
import cx.rain.mc.inkraft.command.CommandConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class StoryWrapper {

    private final Story story;

    public StoryWrapper(Story story) {
        this.story = story;
    }

    public boolean continueStory(ServerPlayer player, IInkStoryStateHolder holder) {
        try {
            if (story.canContinue()) {
                var message = story.Continue();
                player.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.GREEN), true);
                var choices = story.getCurrentChoices();

                var token = UUID.randomUUID();
                holder.setContinueToken(token);

                if (choices.size() == 0) {
                    var component = Component.translatable(CommandConstants.MESSAGE_STORY_CONTINUE);
                    component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inkraft continue " + token));
                    player.sendSystemMessage(component, true);
                } else {
                    for (int i = 0; i < choices.size(); i++) {
                        var choice = choices.get(i);
                        var component = Component.translatable(CommandConstants.MESSAGE_STORY_CONTINUE_CHOICE, choice.getText());
                        component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inkraft continue " + token + " " + i));
                        player.sendSystemMessage(component, true);
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
            ex.printStackTrace();
        }
    }
}
