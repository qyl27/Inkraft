package cx.rain.mc.inkraft.forge.capability;

import cx.rain.mc.inkraft.story.state.IInkStoryStateHolder;
import cx.rain.mc.inkraft.utility.StoryVariables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InkStoryStateHolder implements IInkStoryStateHolder, INBTSerializable<CompoundTag> {

    /// <editor-fold desc="State holder.">

    private UUID continueToken = UUID.randomUUID();
    private String state = "";
    private String lastMessage = "";
    private boolean isInStory = false;

    @Override
    public String getState() {
        return state;
    }

    @Override
    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String getLastMessage() {
        return lastMessage;
    }

    @Override
    public void setLastMessage(String message) {
        lastMessage = message;
    }

    @Override
    public UUID getContinueToken() {
        return continueToken;
    }

    @Override
    public void setContinueToken(UUID token) {
        this.continueToken = token;
    }

    @Override
    public boolean isInStory() {
        return isInStory;
    }

    @Override
    public void setInStory(boolean inStory) {
        this.isInStory = inStory;
    }

    @Override
    public void clearState() {
        setState("");
        setInStory(false);
        setLastMessage("");
    }

    /// </editor-fold>

    /// <editor-fold desc="Variables show.">

    private final Map<String, Triple<String, Boolean, StoryVariables.IStoryVariable>> variables = new HashMap<>();

    @Override
    public Map<String, Triple<String, Boolean, StoryVariables.IStoryVariable>> getVariables() {
        return variables;
    }

    @Override
    public void putVariable(String name, String displayName, boolean isShow, StoryVariables.IStoryVariable value) {
        variables.put(name, new MutableTriple<>(displayName, isShow, value));
    }

    @Override
    public StoryVariables.IStoryVariable getVariable(String name) {
        if (!variables.containsKey(name)) {
            putVariable(name, "", false, StoryVariables.BoolVar.FALSE);
        }
        return variables.get(name).getRight();
    }

    @Override
    public void hideVariables() {
        for (var variable : variables.entrySet()) {
            if (variable.getValue().getMiddle()) {
                var value = variable.getValue();
                putVariable(variable.getKey(), value.getLeft(), false, value.getRight());
            }
        }
    }

    @Override
    public void clearVariables() {
        variables.clear();
    }

    /// </editor-fold>

    private ResourceLocation currentStory = null;

    @Override
    public ResourceLocation getCurrentStory() {
        return currentStory;
    }

    @Override
    public void setCurrentStory(ResourceLocation story) {
        currentStory = story;
    }

    /// <editor-fold desc="NBT (de)serialize.">

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

    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();

        tag.putString(TAG_STATE_NAME, getState());
        tag.putString(TAG_LAST_MESSAGE_NAME, getLastMessage());
        tag.putUUID(TAG_TOKEN_NAME, getContinueToken());
        tag.putBoolean(TAG_IN_STORY_NAME, isInStory());

        var list = new ListTag();
        for (var entry : variables.entrySet()) {
            var compoundTag = new CompoundTag();
            compoundTag.putString(TAG_VARIABLES_NAME_NAME, entry.getKey());
            compoundTag.putString(TAG_VARIABLES_DISPLAY_NAME, entry.getValue().getLeft());
            compoundTag.putBoolean(TAG_VARIABLES_SHOW_NAME, entry.getValue().getMiddle());
            compoundTag.putString(TAG_VARIABLES_VALUE_NAME, entry.getValue().getRight().asString());
            list.add(compoundTag);
        }
        tag.put(TAG_VARIABLES_NAME, list);

        var story = getCurrentStory();
        if (story != null) {
            tag.putString(TAG_CURRENT_STORY_NAME, story.toString());
        }

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        setState(tag.getString(TAG_STATE_NAME));
        setLastMessage(tag.getString(TAG_LAST_MESSAGE_NAME));

        if (tag.contains(TAG_TOKEN_NAME)) {
            setContinueToken(tag.getUUID(TAG_TOKEN_NAME));
        }

        setInStory(tag.getBoolean(TAG_IN_STORY_NAME));

        for (var entry : tag.getList(TAG_VARIABLES_NAME, Tag.TAG_COMPOUND)) {
            var compoundTag = (CompoundTag) entry;
            var name = compoundTag.getString(TAG_VARIABLES_NAME_NAME);
            var displayName = compoundTag.getString(TAG_VARIABLES_DISPLAY_NAME);
            var isShow = compoundTag.getBoolean(TAG_VARIABLES_SHOW_NAME);
            var value = compoundTag.getString(TAG_VARIABLES_VALUE_NAME);
            putVariable(name, displayName, isShow, StoryVariables.IStoryVariable.fromString(value));
        }

        var story = tag.getString(TAG_CURRENT_STORY_NAME);
        if (!story.isBlank()) {
            setCurrentStory(new ResourceLocation(story));
        }
    }

    /// </editor-fold>
}
