package cx.rain.mc.inkraft.story;

import com.bladecoder.ink.runtime.Story;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class StoryWrapper {

    private final Story story;

    public StoryWrapper(Story story) {
        this.story = story;
    }

    public boolean continueStory(ServerPlayer player) {
        try {
            if (story.canContinue()) {
                var message = story.Continue();
                player.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.GREEN), true);
                var choices = story.getCurrentChoices();
                // Todo.

                return true;
            } else {
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
