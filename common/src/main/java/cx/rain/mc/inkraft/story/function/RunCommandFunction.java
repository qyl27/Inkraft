package cx.rain.mc.inkraft.story.function;

import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class RunCommandFunction implements StoryFunction {
    @Override
    public String getName() {
        return "runCommand";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryFunctionResults.IStoryFunctionResult> func(boolean isDebug) {
        return (args, player) -> {
            if (args.length != 1) {
                return new StoryFunctionResults.BoolResult(false);
            }

            var command = args[0].toString();
            var result = player.getServer()
                    .getCommands()
                    .performPrefixedCommand(player.createCommandSourceStack()
                            .withPermission(4), command) != 0;
            return new StoryFunctionResults.BoolResult(result);
        };
    }
}
