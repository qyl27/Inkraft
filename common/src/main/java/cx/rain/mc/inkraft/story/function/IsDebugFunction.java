package cx.rain.mc.inkraft.story.function;

import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class IsDebugFunction implements StoryFunction {
    @Override
    public String getName() {
        return "isDebug";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryFunctionResults.IStoryFunctionResult> func(boolean isDebug) {
        return (args, player) -> new StoryFunctionResults.BoolResult(isDebug);
    }
}
