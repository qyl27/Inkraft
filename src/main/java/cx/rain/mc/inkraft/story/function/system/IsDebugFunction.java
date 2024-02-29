package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.story.PlayerStoryState;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.utility.StoryVariable;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class IsDebugFunction implements StoryFunction {
    @Override
    public String getName() {
        return "isDebug";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryVariable.IValue> func(PlayerStoryState engine) {
        return (args, player) -> new StoryVariable.BoolVar(engine.isDebug());
    }
}
