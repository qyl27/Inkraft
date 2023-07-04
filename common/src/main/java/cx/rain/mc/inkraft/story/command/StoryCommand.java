package cx.rain.mc.inkraft.story.command;

import cx.rain.mc.inkraft.story.StoryEngine;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface StoryCommand {
    default String getName() {
        return "";
    }

    default int getArgumentsCount() {
        return 0;
    }

    BiConsumer<String[], ServerPlayer> getConsumer(StoryEngine story);
}
