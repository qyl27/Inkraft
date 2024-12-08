package cx.rain.mc.inkraft.platform.impl;

import com.google.common.collect.ImmutableMap;
import cx.rain.mc.inkraft.platform.IInkPlayerData;
import cx.rain.mc.inkraft.story.IStoryVariable;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InkPlayerData implements IInkPlayerData {
    private ResourceLocation story;
    private String state;
    private boolean ended;
    private UUID continuousToken;
    private final Map<String, IStoryVariable<?>> variables = new HashMap<>();

    @Override
    public @Nullable ResourceLocation getStory() {
        return story;
    }

    @Override
    public void setStory(@Nullable ResourceLocation story) {
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
    public boolean hasVariable(@NotNull String name) {
        return variables.containsKey(name);
    }

    @Override
    public @Nullable IStoryVariable<?> getVariable(@NotNull String name) {
        return variables.get(name);
    }

    @Override
    public void setVariable(@NotNull String name, @NotNull IStoryVariable<?> value) {
        variables.put(name, value);
    }

    @Override
    public void unsetVariable(@NotNull String name) {
        variables.remove(name);
    }

    @Override
    public @NotNull Map<String, IStoryVariable<?>> getVariables() {
        return ImmutableMap.copyOf(variables);
    }

    @Override
    public void clearVariables() {
        variables.clear();
    }
}
