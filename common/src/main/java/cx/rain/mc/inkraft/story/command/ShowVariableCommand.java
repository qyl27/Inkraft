package cx.rain.mc.inkraft.story.command;

import cx.rain.mc.inkraft.story.StoryEngine;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;

public class ShowVariableCommand implements StoryCommand {
    @Override
    public String getName() {
        return "SHOW_VARIABLE";
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

            var name = args[0];
            var variable = story.getVariablesState().get(name);


        };
    }
}
