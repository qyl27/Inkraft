package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.story.StoryEngine;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.story.function.StoryFunctionResults;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class HideContinueFunction implements StoryFunction {
    @Override
    public String getName() {
        return "hideContinue";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryFunctionResults.IStoryFunctionResult> func(StoryEngine engine) {
        return (args, player) -> {
            if (args.length != 1) {
                return StoryFunctionResults.BoolResult.FALSE;
            }

            var isHide = args[0].toString().equalsIgnoreCase("true");
            engine.setHideContinue(isHide);
            return StoryFunctionResults.BoolResult.TRUE;
        };
    }
}
