package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.story.PlayerStory;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.utility.StoryVariables;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class PutVariableFunction implements StoryFunction {
    @Override
    public String getName() {
        return "putVariable";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryVariables.IValue> func(PlayerStory engine) {
        return (args, player) -> {
            if (args.length != 2) {
                return StoryVariables.BoolVar.FALSE;
            }

            var name = args[0].toString();
            var value = args[1].toString();

            InkraftPlatform.getPlayerStoryStateHolder(player).putVariable(name, "", false, StoryVariables.IValue.fromString(value));

            return StoryVariables.BoolVar.TRUE;
        };
    }
}
