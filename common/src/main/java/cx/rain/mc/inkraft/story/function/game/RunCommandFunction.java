package cx.rain.mc.inkraft.story.function.game;

import cx.rain.mc.inkraft.story.StoryEngine;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.utility.StoryVariables;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class RunCommandFunction implements StoryFunction {
    @Override
    public String getName() {
        return "runCommand";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryVariables.IStoryVariable> func(StoryEngine engine) {
        return (args, player) -> {
            if (args.length != 1) {
                return new StoryVariables.BoolVar(false);
            }

            var command = args[0].toString();
            var result = player.getServer()
                    .getCommands()
                    .performPrefixedCommand(player.createCommandSourceStack()
                            .withPermission(4), command) == 1;
            return new StoryVariables.BoolVar(result);
        };
    }
}
