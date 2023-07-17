package cx.rain.mc.inkraft.story.function;

import cx.rain.mc.inkraft.story.StoryEngine;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

@FunctionalInterface
public interface StoryFunction {
    default String getName() {
        return "";
    }

    BiFunction<Object[], ServerPlayer, StoryFunctionResults.IStoryFunctionResult> func(StoryEngine engine);
}
