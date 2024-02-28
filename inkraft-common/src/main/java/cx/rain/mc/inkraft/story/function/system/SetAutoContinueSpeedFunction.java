package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.story.PlayerStory;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.story.StoryVariables;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class SetAutoContinueSpeedFunction implements StoryFunction {
    @Override
    public String getName() {
        return "setAutoContinueSpeed";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryVariables.IValue> func(PlayerStory engine) {
        return (args, player) -> {
            if (args.length != 1) {
                return StoryVariables.BoolVar.FALSE;
            }

            try {
                var speed = Long.parseLong(args[0].toString());
                engine.setContinueSpeed(speed);
                return StoryVariables.BoolVar.TRUE;
            } catch (NumberFormatException ex) {
                return StoryVariables.BoolVar.FALSE;
            }
        };
    }
}
