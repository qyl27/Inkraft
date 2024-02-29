package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.story.PlayerStoryState;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.utility.StoryVariable;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class GetVariableFunction implements StoryFunction {
    @Override
    public String getName() {
        return "getVariable";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryVariable.IValue> func(PlayerStoryState engine) {
        return (args, player) -> {
            if (args.length != 1) {
                return StoryVariable.BoolVar.FALSE;
            }

            var name = args[0].toString();
            return InkraftPlatform.getPlayerStoryHolder(player).getVariable(name);
        };
    }
}
