package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.story.PlayerStoryState;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.utility.StoryVariable;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class SetAutoContinueSpeedFunction implements StoryFunction {
    @Override
    public String getName() {
        return "setAutoContinueSpeed";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryVariable.IValue> func(PlayerStoryState engine) {
        return (args, player) -> {
            if (args.length != 1) {
                return StoryVariable.BoolVar.FALSE;
            }

            try {
                var speed = Long.parseLong(args[0].toString());
                engine.setContinueSpeed(speed);
                return StoryVariable.BoolVar.TRUE;
            } catch (NumberFormatException ex) {
                return StoryVariable.BoolVar.FALSE;
            }
        };
    }
}
