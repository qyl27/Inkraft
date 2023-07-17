package cx.rain.mc.inkraft.forge.capability;

import com.mojang.datafixers.util.Pair;
import cx.rain.mc.inkraft.story.state.IInkStoryStateHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InkStoryStateHolder implements IInkStoryStateHolder, INBTSerializable<CompoundTag> {

    /// <editor-fold desc="State holder.">

    private String state = "";
    private UUID continueToken = UUID.randomUUID();
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
    }

    /// </editor-fold>

    /// <editor-fold desc="Variables show.">

    private final Map<String, Pair<String, String>> variables = new HashMap<>();

    @Override
    public Map<String, Pair<String, String>> getVariables() {
        return variables;
    }

    @Override
    public void putVariable(String name, String displayName, boolean isShow, String value) {
        if (isShow) {
            variables.put(name, new Pair<>(displayName, value));
        } else {
            variables.remove(name);
        }
    }

    @Override
    public void clearShowedVariables() {
        variables.clear();
    }

    /// </editor-fold>

    /// <editor-fold desc="NBT (de)serialize.">

    public static final String TAG_STATE_NAME = "state";
    public static final String TAG_TOKEN_NAME = "token";
    public static final String TAG_IN_STORY_NAME = "inStory";
    public static final String TAG_VARIABLES_NAME = "variables";
    public static final String TAG_VARIABLES_NAME_NAME = "name";
    public static final String TAG_VARIABLES_DISPLAY_NAME = "displayDame";
    public static final String TAG_VARIABLES_VALUE_NAME = "value";

    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();

        tag.putString(TAG_STATE_NAME, getState());
        tag.putUUID(TAG_TOKEN_NAME, getContinueToken());
        tag.putBoolean(TAG_IN_STORY_NAME, isInStory());

        var list = new ListTag();
        for (var entry : variables.entrySet()) {
            var compoundTag = new CompoundTag();
            compoundTag.putString(TAG_VARIABLES_NAME_NAME, entry.getKey());
            compoundTag.putString(TAG_VARIABLES_DISPLAY_NAME, entry.getValue().getFirst());
            compoundTag.putString(TAG_VARIABLES_VALUE_NAME, entry.getValue().getSecond());
            list.add(compoundTag);
        }
        tag.put(TAG_VARIABLES_NAME, list);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        setState(tag.getString(TAG_STATE_NAME));
        setContinueToken(tag.getUUID(TAG_TOKEN_NAME));
        setInStory(tag.getBoolean(TAG_IN_STORY_NAME));

        for (var entry : tag.getList(TAG_VARIABLES_NAME, Tag.TAG_COMPOUND)) {
            var compoundTag = (CompoundTag) entry;
            var name = compoundTag.getString(TAG_VARIABLES_NAME_NAME);
            var displayName = compoundTag.getString(TAG_VARIABLES_DISPLAY_NAME);
            var value = compoundTag.getString(TAG_VARIABLES_VALUE_NAME);
            putVariable(name, displayName, true, value);
        }
    }

    /// </editor-fold>
}
