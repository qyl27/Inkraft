package cx.rain.mc.inkraft.fabric.platform;

import cx.rain.mc.inkraft.fabric.mixins.interfaces.IPlayerMixin;
import cx.rain.mc.inkraft.story.state.IInkStoryStateHolder;
import cx.rain.mc.inkraft.utility.StoryVariables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Map;
import java.util.UUID;

public class InkStoryStateHolderFabric implements IInkStoryStateHolder {
    public static final String TAG_INKRAFT_NAME = "inkraft";
    public static final String TAG_STATE_NAME = "state";
    public static final String TAG_LAST_MESSAGE_NAME = "lastMessage";
    public static final String TAG_TOKEN_NAME = "token";
    public static final String TAG_IN_STORY_NAME = "inStory";
    public static final String TAG_VARIABLES_NAME = "variables";
    public static final String TAG_VARIABLES_NAME_NAME = "name";
    public static final String TAG_VARIABLES_DISPLAY_NAME = "displayName";
    public static final String TAG_VARIABLES_SHOW_NAME = "isShow";
    public static final String TAG_VARIABLES_VALUE_NAME = "value";
    public static final String TAG_CURRENT_STORY_NAME = "currentStory";

    private Player player;

    public InkStoryStateHolderFabric(Player player) {
        this.player = player;
    }

    @Override
    public String getState() {
        return ((IPlayerMixin) player).inkraft$getState();
    }

    @Override
    public void setState(String state) {
        ((IPlayerMixin) player).inkraft$setState(state);
    }

    @Override
    public String getLastMessage() {
        return ((IPlayerMixin) player).inkraft$getLastMessage();
    }

    @Override
    public void setLastMessage(String message) {
        ((IPlayerMixin) player).inkraft$setLastMessage(message);
    }

    @Override
    public UUID getContinueToken() {
        return ((IPlayerMixin) player).inkraft$getContinueToken();
    }

    @Override
    public void setContinueToken(UUID token) {
        ((IPlayerMixin) player).inkraft$setContinueToken(token);
    }

    @Override
    public boolean isInStory() {
        return ((IPlayerMixin) player).inkraft$isInStory();
    }

    @Override
    public void setInStory(boolean inStory) {
        ((IPlayerMixin) player).inkraft$setInStory(inStory);
    }

    @Override
    public void clearState() {
        setState("");
        setInStory(false);
        setLastMessage("");
    }

    @Override
    public Map<String, Triple<String, Boolean, StoryVariables.IStoryVariable>> getVariables() {
        return ((IPlayerMixin) player).inkraft$getVariables();
    }

    @Override
    public void putVariable(String name, String displayName, boolean isShow, StoryVariables.IStoryVariable value) {
        ((IPlayerMixin) player).inkraft$putVariable(name, displayName, isShow, value);
    }

    @Override
    public StoryVariables.IStoryVariable getVariable(String name) {
        return ((IPlayerMixin) player).inkraft$getVariable(name);
    }

    @Override
    public void hideVariables() {
        ((IPlayerMixin) player).inkraft$hideVariables();
    }

    @Override
    public void clearVariables() {
        ((IPlayerMixin) player).inkraft$clearVariables();
    }

    @Override
    public ResourceLocation getCurrentStory() {
        return ((IPlayerMixin) player).inkraft$getCurrentStory();
    }

    @Override
    public void setCurrentStory(ResourceLocation story) {
        ((IPlayerMixin) player).inkraft$setCurrentStory(story);
    }
}
