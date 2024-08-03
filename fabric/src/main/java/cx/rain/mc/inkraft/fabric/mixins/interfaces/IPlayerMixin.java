package cx.rain.mc.inkraft.fabric.mixins.interfaces;

import cx.rain.mc.inkraft.story.IStoryVariable;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Map;
import java.util.UUID;

public interface IPlayerMixin {
    String inkraft$getState();
    void inkraft$setState(String state);

    String inkraft$getLastMessage();
    void inkraft$setLastMessage(String message);

    UUID inkraft$getContinueToken();
    void inkraft$setContinueToken(UUID token);

    boolean inkraft$isInStory();
    void inkraft$setInStory(boolean inStory);

    Map<String, Triple<String, Boolean, IStoryVariable.IStoryVariable>> inkraft$getVariables();
    void inkraft$putVariable(String name, String displayName, boolean isShow, IStoryVariable.IStoryVariable value);
    IStoryVariable.IStoryVariable inkraft$getVariable(String name);
    void inkraft$hideVariables();
    void inkraft$clearVariables();

    ResourceLocation inkraft$getCurrentStory();
    void inkraft$setCurrentStory(ResourceLocation story);

    boolean inkraft$getCurrentAutoContinue();
    void inkraft$setCurrentAutoContinue(boolean autoContinue);
    long inkraft$getCurrentAutoContinueSpeed();
    void inkraft$setCurrentAutoContinueSpeed(long autoContinueSpeed);
}
