package cx.rain.mc.inkraft.story.function.game.command;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.atomic.AtomicInteger;

public class RunCommandFunction implements IStoryFunction {

    @Override
    public String getName() {
        return "runCommand";
    }

    @Override
    public IStoryVariable.Int apply(StoryInstance instance, Object... args) {
        var command = args[0].toString();
        var source = instance.getPlayer().createCommandSourceStack();
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

    public static class UnlimitedCommand implements IStoryFunction {

        @Override
        public String getName() {
            return "runUnlimitedCommand";
        }

        @Override
        public IStoryVariable.Int apply(StoryInstance instance, Object... args) {
            var command = args[0].toString();
            var source = instance.getPlayer().createCommandSourceStack().withPermission(4);
            var server = instance.getPlayer().getServer();
            assert server != null;
            return new IStoryVariable.Int(execute(command, server, source));
        }
    }

    public static class ServerCommand implements IStoryFunction {

        @Override
        public String getName() {
            return "runServerCommand";
        }

        @Override
        public IStoryVariable.Int apply(StoryInstance instance, Object... args) {
            var command = args[0].toString();
            var server = instance.getPlayer().getServer();
            assert server != null;
            var source = server.createCommandSourceStack();
            return new IStoryVariable.Int(execute(command, server, source));
        }
    }
}
