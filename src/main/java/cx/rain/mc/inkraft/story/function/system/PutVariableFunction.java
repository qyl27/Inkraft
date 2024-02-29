package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.story.PlayerStoryState;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.utility.StoryVariable;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class PutVariableFunction implements StoryFunction {
    @Override
    public String getName() {
        return "putVariable";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryVariable.IValue> func(PlayerStoryState engine) {
        return (args, player) -> {
            if (args.length != 2) {
                return StoryVariable.BoolVar.FALSE;
            }

            var name = args[0].toString();
            var value = args[1].toString();

            InkraftPlatform.getPlayerStoryHolder(player).putVariable(name, "", false, StoryVariable.IValue.fromString(value));

            return StoryVariable.BoolVar.TRUE;
        };
    }
}
