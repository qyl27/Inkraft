package cx.rain.mc.inkraft.storage;

import com.google.common.collect.ImmutableMap;
import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.api.platform.storage.IInkPlayerData;
import cx.rain.mc.inkraft.api.platform.storage.IValueIOSerializable;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.value.InkListStoryValue;
import cx.rain.mc.inkraft.story.value.StringStoryValue;
import cx.rain.mc.inkraft.story.value.StringifyStoryValue;
import cx.rain.mc.inkraft.utility.IdHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InkPlayerData implements IInkPlayerData, IValueIOSerializable {
    @Nullable
    private Identifier story;
    @Nullable
    private String state;
    private boolean ended = true;
    @Nullable
    private UUID continuousToken;   // Won't be serialized.

    private final Map<String, StoredInkVariable> variables = new HashMap<>();

    @Override
    public @Nullable Identifier getStory() {
        return story;
    }

    @Override
    public void setStory(@Nullable Identifier story) {
        this.story = story;
    }

    @Override
    public @Nullable String getState() {
        return state;
    }

    @Override
    public void setState(@Nullable String state) {
        this.state = state;
    }

    @Override
    public boolean isEnded() {
        return ended;
    }

    @Override
    public void setEnded(boolean end) {
        this.ended = end;
    }

    @Override
    public @Nullable UUID getContinuousToken() {
        return continuousToken;
    }

    @Override
    public void setContinuousToken(@Nullable UUID token) {
        this.continuousToken = token;
    }

    @Override
    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }

    @Override
    public @Nullable IStoryValue<?, ?> getVariable(String name) {
        var variable = variables.get(name);
        if (variable == null) {
            return null;
        }
        return variable.value();
    }

    @Override
    public void setVariable(String name, IStoryValue<?, ?> value) {
        if (value instanceof StringifyStoryValue<?>
            || value instanceof InkListStoryValue) {
            value = new StringStoryValue(value.getString());
        }
        var variable = new StoredInkVariable(name, value);
        variables.put(name, variable);
    }

    @Override
    public void unsetVariable(String name) {
        variables.remove(name);
    }

    @Override
    public Map<String, IStoryValue<?, ?>> getVariables() {
        var builder = ImmutableMap.<String, IStoryValue<?, ?>>builder();
        for (var entry : variables.entrySet()) {
            builder.put(entry.getKey(), entry.getValue().value());
        }
        return builder.build();
    }

    @Override
    public void clearVariables() {
        variables.clear();
    }


    // region IValueIOSerializable

    @ApiStatus.Internal
    protected List<StoredInkVariable> getStoredInkVariables() {
        return variables.values().stream().toList();
    }

    @ApiStatus.Internal
    protected void setStoredInkVariables(List<StoredInkVariable> variables) {
        this.variables.clear();
        for (var v : variables) {
            this.variables.put(v.name(), v);
        }
    }

    @Override
    public void serialize(ValueOutput output) {
        var story = getStory();
        if (story != null) {
            output.putString(ModConstants.Tags.STORY, story.toString());
        }
        var state = getState();
        if (state != null) {
            output.putString(ModConstants.Tags.STATE, state);
        }
        output.putBoolean(ModConstants.Tags.ENDED, isEnded());
        output.store(ModConstants.Tags.VARIABLES, StoredInkVariable.LIST_CODEC, getStoredInkVariables());
    }

    @Override
    public void deserialize(ValueInput input) {
        clearData();
        input.getString(ModConstants.Tags.STORY).map(IdHelper::of).ifPresent(this::setStory);
        input.getString(ModConstants.Tags.STATE).ifPresent(this::setState);
        setEnded(input.getBooleanOr(ModConstants.Tags.ENDED, true));
        input.read(ModConstants.Tags.VARIABLES, StoredInkVariable.LIST_CODEC)
            .ifPresent(this::setStoredInkVariables);
    }
}
