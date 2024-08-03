package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.story.IStoryVariable;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class SetAutoContinueSpeedFunction implements StoryFunction {
    @Override
    public String getName() {
        return "setAutoContinueSpeed";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, IStoryVariable.IStoryVariable> apply(StoryInstance engine, Object... args) {
        return (args, player) -> {
            if (args.length != 1) {
                return IStoryVariable.Bool.FALSE;
            }

            try {
                var speed = Long.parseLong(args[0].toString());
                engine.setContinueSpeed(speed);
                return IStoryVariable.Bool.TRUE;
            } catch (NumberFormatException ex) {
                return IStoryVariable.Bool.FALSE;
            }
        };
    }
}
