package cx.rain.mc.inkraft.story.function.game;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cx.rain.mc.inkraft.story.PlayerStoryState;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.utility.StoryVariable;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;
import java.util.function.BiFunction;

public class RunCommandFunction implements StoryFunction {
    @Override
    public String getName() {
        return "runCommand";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryVariable.IValue> func(PlayerStoryState engine) {
        return (args, player) -> {
            if (args.length != 1) {
                return new StoryVariable.BoolVar(false);
            }

            var command = args[0].toString();
            var dispatcher = Objects.requireNonNull(player.getServer()).getCommands().getDispatcher();
            try {
                boolean result = dispatcher.execute(dispatcher
                        .parse(command, player.createCommandSourceStack().withPermission(4))) == 1;
                return new StoryVariable.BoolVar(result);
            } catch (CommandSyntaxException ex) {
                return new StoryVariable.BoolVar(false);
            }
        };
    }
}
