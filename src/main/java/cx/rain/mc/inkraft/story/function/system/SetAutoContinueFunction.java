package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.story.IStoryVariable;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class SetAutoContinueFunction implements StoryFunction {
    @Override
    public String getName() {
        return "setAutoContinue";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, IStoryVariable.IStoryVariable> apply(StoryInstance engine, Object... args) {
        return (args, player) -> {
            if (args.length != 1) {
                return IStoryVariable.Bool.FALSE;
            }

            var isAuto = args[0].toString().equalsIgnoreCase("true");
            engine.setAutoContinue(isAuto);
            return IStoryVariable.Bool.TRUE;
        };
    }
}
