package cx.rain.mc.inkraft.story.function.game.command.storage;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.utility.StringArgumentParseHelper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetStorageFunction implements IStoryFunction {

    @Override
    public String getName() {
        return "setStorage";
    }

    @Override
    public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.expectCount(args, 3);
        var idArgument = FunctionArgs.requireString(args[0]);
        var pathArgument = FunctionArgs.requireString(args[1]);
        var valueArgument = FunctionArgs.requireString(args[2]);

        var server = instance.getPlayer().level().getServer();
        var id = StringArgumentParseHelper.parseId(idArgument);
        var storage = server.getCommandStorage();
        var tag = storage.get(id);

        try {
            var path = StringArgumentParseHelper.parseNbtPath(pathArgument);
            var value = StringArgumentParseHelper.parseNbt(valueArgument);
            path.set(tag, value);
            storage.set(id, tag);
            return BoolStoryValue.TRUE;
        } catch (CommandSyntaxException ex) {
            log.warn("NBT Path Error: ", ex);
            return BoolStoryValue.FALSE;
        }
    }
}
