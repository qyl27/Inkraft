package cx.rain.mc.inkraft.story.command;

import cx.rain.mc.inkraft.story.StoryEngine;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;

public class CommandCommand implements StoryCommand {
    @Override
    public String getName() {
        return "COMMAND";
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public BiConsumer<String[], ServerPlayer> getConsumer(StoryEngine story) {
        return (args, player) -> {
            if (args.length != 1) {
                return;
            }

            var command = args[0];

            player.getServer()
                    .getCommands()
                    .performPrefixedCommand(player.getServer().createCommandSourceStack(), command);
        };
    }
}
