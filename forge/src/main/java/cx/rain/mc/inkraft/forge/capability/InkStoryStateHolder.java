package cx.rain.mc.inkraft.forge.capability;

import cx.rain.mc.inkraft.story.IInkStoryStateHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public class InkStoryStateHolder implements IInkStoryStateHolder, INBTSerializable<CompoundTag> {
    public static final String TAG_STATE_NAME = "state";
    public static final String TAG_TOKEN_NAME = "token";
    public static final String TAG_IN_STORY_NAME = "inStory";

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
        this.isInStory = isInStory;
    }

    @Override
    public void clearState() {
        setState("");
        setInStory(false);
    }

    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();

        tag.putString(TAG_STATE_NAME, getState());
        tag.putUUID(TAG_TOKEN_NAME, getContinueToken());
        tag.putBoolean(TAG_IN_STORY_NAME, isInStory());

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        setState(tag.getString(TAG_STATE_NAME));
        setContinueToken(tag.getUUID(TAG_TOKEN_NAME));
        setInStory(tag.getBoolean(TAG_IN_STORY_NAME));
    }
}
