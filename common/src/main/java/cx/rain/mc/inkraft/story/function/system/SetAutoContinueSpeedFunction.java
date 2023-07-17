package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.story.StoryEngine;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.story.function.StoryFunctionResults;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class SetAutoContinueSpeedFunction implements StoryFunction {
    @Override
    public String getName() {
        return "setAutoContinueSpeed";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryFunctionResults.IStoryFunctionResult> func(StoryEngine engine) {
        return (args, player) -> {
            if (args.length != 1) {
                return StoryFunctionResults.BoolResult.FALSE;
            }

            try {
                var speed = Long.parseLong(args[0].toString());
                engine.setContinueSpeed(speed);
                return StoryFunctionResults.BoolResult.TRUE;
            } catch (NumberFormatException ex) {
                return StoryFunctionResults.BoolResult.FALSE;
            }
        };
    }
}
