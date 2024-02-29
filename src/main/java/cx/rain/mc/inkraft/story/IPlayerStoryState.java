package cx.rain.mc.inkraft.story;

import cx.rain.mc.inkraft.utility.ICompoundSerializable;
import cx.rain.mc.inkraft.utility.StoryVariable;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.UUID;

public interface IPlayerStoryState extends ICompoundSerializable {
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

    boolean isPausing();
    void setPausing(boolean pause);

    boolean isEnd();
    void setEnd(boolean end);

    Map<String, StoryVariable.IValue> getVariables();
    void putVariable(String name, StoryVariable.IValue value);
    StoryVariable.IValue getVariable(String name);
    void removeVariable(String name);

}
