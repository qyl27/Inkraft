package cx.rain.mc.inkraft.api.platform.storage;

import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.storage.StoredInkVariable;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public interface IInkPlayerData {
    @Nullable
    Identifier getStory();

    void setStory(@Nullable Identifier story);

    @Nullable
    String getState();

    void setState(@Nullable String state);

    boolean isEnded();

    void setEnded(boolean end);

    @Nullable
    UUID getContinuousToken();

    void setContinuousToken(@Nullable UUID token);

    boolean hasVariable(String name);

    @Nullable
    IStoryValue<?, ?> getVariable(String name);

    void setVariable(String name, IStoryValue<?, ?> value);

    void unsetVariable(String name);

    Map<String, IStoryValue<?, ?>> getVariables();

    void clearVariables();

    default boolean hasData() {
        return getStory() != null && getState() != null;
    }

    default void resetState() {
        setState(null);
//        setEnded(true);
        setContinuousToken(null);
    }

    default void clearData() {
        setStory(null);
        resetState();
        clearVariables();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    default <U, T extends IStoryValue<U, ?>> U getVariable(String name, Class<T> type) {
        var v = getVariable(name);
        if (v != null && v.getClass().equals(type)) {
            return ((T) v).getValue();
        }
        return null;
    }
}
