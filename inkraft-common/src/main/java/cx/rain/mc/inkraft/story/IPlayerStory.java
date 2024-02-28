package cx.rain.mc.inkraft.story;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.UUID;

public interface IPlayerStory {
    ResourceLocation getStoryPath();
    void setStoryPath(ResourceLocation path);

    String getState();
    void setState(String state);

    UUID getStepToken();
    void setStepToken(UUID token);

    /**
     * Get next choice of story.
     * To prevent eager continue.
     * @return @see IPlayerStory#getNextChoice
     */
    int getNextStep();

    /**
     * Add next choice.
     * @param next choice index, -1 for no choice to decide.
     */
    void addNextStep(int next);

    /**
     * @return Has next choice.
     */
    boolean hasNextStep();

    String getPreviousMessage();
    void setPreviousMessage(String message);

    boolean isEnd();
    void setEnd(boolean end);

    Map<String, StoryVariables.IValue> getVariables();
    void putVariable(String name, StoryVariables.IValue value);
    StoryVariables.IValue getVariable(String name);


}
