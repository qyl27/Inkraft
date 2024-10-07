package cx.rain.mc.inkraft.story.function.game.command;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class RunCommandFunction implements IStoryFunction {
    private final String name;
    private final Function<ServerPlayer, CommandSourceStack> function;

    public RunCommandFunction(String name, Function<ServerPlayer, CommandSourceStack> function) {
        this.name = name;
        this.function = function;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IStoryVariable.Int apply(StoryInstance instance, Object... args) {
        var command = args[0].toString();
        var source = function.apply(instance.getPlayer());
        var server = instance.getPlayer().getServer();
        assert server != null;
        return new IStoryVariable.Int(execute(command, server, source));
    }

    private static int execute(String command, MinecraftServer server, CommandSourceStack commandSourceStack) {
        var result = new AtomicInteger();
        commandSourceStack.withCallback((success, ret) -> result.set(ret), CommandResultCallback::chain);
        server.getCommands().performPrefixedCommand(commandSourceStack, command);
        return result.get();
    }
}
