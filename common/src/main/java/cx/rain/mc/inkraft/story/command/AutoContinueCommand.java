package cx.rain.mc.inkraft.story.command;

import cx.rain.mc.inkraft.story.StoryEngine;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;

public class AutoContinueCommand implements StoryCommand {
    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public String getName() {
        return "AUTO_CONTINUE";
    }

    @Override
    public BiConsumer<String[], ServerPlayer> getConsumer(StoryEngine story) {
        return (args, player) -> {
            if (args.length != 1) {
                return;
            }

            var arg = args[0];
            story.setAutoContinue(player, arg.equalsIgnoreCase("true"));
        };
    }
}
