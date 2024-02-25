package cx.rain.mc.inkraft.story.state;

import cx.rain.mc.inkraft.utility.StoryVariables;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Map;
import java.util.UUID;

public interface IInkStoryStateHolder {
    String getState();
    void setState(String state);

    String getLastMessage();
    void setLastMessage(String message);

    UUID getContinueToken();
    void setContinueToken(UUID token);

    boolean isInStory();
    void setInStory(boolean inStory);

    void clearState();

    Map<String, Triple<String, Boolean, StoryVariables.IStoryVariable>> getVariables();
    void putVariable(String name, String displayName, boolean isShow, StoryVariables.IStoryVariable value);
    StoryVariables.IStoryVariable getVariable(String name);
    void hideVariables();
    void clearVariables();

    ResourceLocation getCurrentStory();
    void setCurrentStory(ResourceLocation story);
    boolean getCurrentAutoContinue();
    void setCurrentAutoContinue(boolean autoContinue);
    long getCurrentAutoContinueSpeed();
    void setCurrentAutoContinueSpeed(long autoContinueSpeed);
}
