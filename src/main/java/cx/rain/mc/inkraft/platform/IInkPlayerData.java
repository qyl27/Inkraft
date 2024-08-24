package cx.rain.mc.inkraft.platform;

import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.story.IStoryVariable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public interface IInkPlayerData {
    @Nullable
    ResourceLocation getStory();
    void setStory(@Nullable ResourceLocation story);

    @Nullable
    String getState();
    void setState(@Nullable String state);

    @Nullable
    UUID getContinuousToken();
    void setContinuousToken(@Nullable UUID token);

    boolean hasVariable(@NotNull String name);
    @Nullable
    IStoryVariable<?> getVariable(@NotNull String name);
    void setVariable(@NotNull String name, @NotNull IStoryVariable<?> value);
    void unsetVariable(@NotNull String name);
    @NotNull Map<String, IStoryVariable<?>> getVariables();
    void clearVariables();

    default boolean hasData() {
        return getStory() != null && getState() != null;
    }

    default void resetState() {
        setState(null);
        setContinuousToken(null);
    }

    default void clearData() {
        setStory(null);
        resetState();
        clearVariables();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    default <U, T extends IStoryVariable<U>> U getVariable(@NotNull String name, Class<T> type) {
        var v = getVariable(name);
        if (v != null && v.getClass().equals(type)) {
            return ((T) v).getValue();
        }
        return null;
    }

    default CompoundTag serialize() {
        var tag = new CompoundTag();

        var story = getStory();
        if (story != null) {
            tag.putString(ModConstants.Tags.STORY, story.toString());
        } else {
            tag.putString(ModConstants.Tags.STORY, "");
        }

        var state = getState();
        tag.putString(ModConstants.Tags.STATE, Objects.requireNonNullElse(state, ""));

        var list = new ListTag();
        for (var entry : getVariables().entrySet()) {
            var item = new CompoundTag();
            item.putString(ModConstants.Tags.VARIABLE_ITEM_NAME, entry.getKey());
            item.putString(ModConstants.Tags.VARIABLE_ITEM_VALUE, entry.getValue().getValue().toString());
            list.add(item);
        }
        tag.put(ModConstants.Tags.VARIABLES, list);
        return tag;
    }

    default void deserialize(CompoundTag tag) {
        if (tag.contains(ModConstants.Tags.STORY)) {
            var str = tag.getString(ModConstants.Tags.STORY);
            if (str.isBlank()) {
                setStory(null);
            } else {
                setStory(ResourceLocation.parse(str));
            }
        }

        if (tag.contains(ModConstants.Tags.STATE)) {
            var str = tag.getString(ModConstants.Tags.STATE);
            if (str.isBlank()) {
                setState(null);
            } else {
                setState(str);
            }
        }

        var list = tag.getList(ModConstants.Tags.VARIABLES, ListTag.TAG_COMPOUND);
        for (var entry : list) {
            if (entry instanceof CompoundTag compound) {
                var name = compound.getString(ModConstants.Tags.VARIABLE_ITEM_NAME);
                var value = IStoryVariable.fromString(compound.getString(ModConstants.Tags.VARIABLE_ITEM_VALUE));
                setVariable(name, value);
            }
        }
    }
}
