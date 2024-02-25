package cx.rain.mc.inkraft.story.function;

import cx.rain.mc.inkraft.story.StoryEngine;
import cx.rain.mc.inkraft.utility.StoryVariables;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

@FunctionalInterface
public interface StoryFunction {
    default String getName() {
        return "";
    }

    BiFunction<Object[], ServerPlayer, StoryVariables.IStoryVariable> func(StoryEngine engine);
}
