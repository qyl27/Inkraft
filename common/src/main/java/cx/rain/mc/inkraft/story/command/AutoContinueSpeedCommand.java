package cx.rain.mc.inkraft.story.command;

import cx.rain.mc.inkraft.story.StoryEngine;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;

public class AutoContinueSpeedCommand implements StoryCommand {
    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public String getName() {
        return "AUTO_CONTINUE_SPEED";
    }

    @Override
    public BiConsumer<String[], ServerPlayer> getConsumer(StoryEngine story) {
        return (args, player) -> {
            if (args.length != 1) {
                return;
            }

            try {
                var speed = Long.parseLong(args[0]);
                story.setContinueSpeed(speed);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };
    }
}
