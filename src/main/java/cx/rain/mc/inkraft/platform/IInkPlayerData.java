package cx.rain.mc.inkraft.platform;

import cx.rain.mc.inkraft.story.IStoryVariable;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
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
    IStoryVariable getVariable(@NotNull String name);
    void setVariable(@NotNull String name, @NotNull IStoryVariable value);
    void unsetVariable(@NotNull String name);
    @NotNull Map<String, IStoryVariable> getVariables();
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
}
