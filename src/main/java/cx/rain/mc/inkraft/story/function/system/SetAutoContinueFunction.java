package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.story.StoryEngine;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.utility.StoryVariables;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class SetAutoContinueFunction implements StoryFunction {
    @Override
    public String getName() {
        return "setAutoContinue";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryVariables.IStoryVariable> func(StoryEngine engine) {
        return (args, player) -> {
            if (args.length != 1) {
                return StoryVariables.BoolVar.FALSE;
            }

            var isAuto = args[0].toString().equalsIgnoreCase("true");
            engine.setAutoContinue(isAuto);
            return StoryVariables.BoolVar.TRUE;
        };
    }
}
