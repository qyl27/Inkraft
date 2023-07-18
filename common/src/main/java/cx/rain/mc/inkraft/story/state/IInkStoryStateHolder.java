package cx.rain.mc.inkraft.story.state;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

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

    Map<String, Pair<String, String>> getVariables();
    void putVariable(String name, String displayName, boolean isShow, String value);
    void clearShowedVariables();

    ResourceLocation getCurrentStory();
    void setCurrentStory(ResourceLocation story);
}
