package cx.rain.mc.inkraft.story.function.game.command.storage;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.utility.StringArgumentParseHelper;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.nbt.*;

@Slf4j
public class SetStorageFunction implements IStoryFunction {

    @Override
    public String getName() {
        return "setStorage";
    }

    @Override
    public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.requireCount(args, 3);
        FunctionArgs.requireTyped(args, 0, String.class);
        FunctionArgs.requireTyped(args, 1, String.class);
        FunctionArgs.requireTyped(args, 2, String.class);

        var server = instance.getPlayer().level().getServer();
        var id = StringArgumentParseHelper.parseId(FunctionArgs.getString(args[0]));
        var storage = server.getCommandStorage();
        var tag = storage.get(id);

        try {
            var path = StringArgumentParseHelper.parseNbtPath(FunctionArgs.getString(args[1]));
            var value = StringArgumentParseHelper.parseNbt(FunctionArgs.getString(args[2]));
            path.set(tag, value);
            storage.set(id, tag);
            return BoolStoryValue.TRUE;
        } catch (CommandSyntaxException ex) {
            log.warn("NBT Path Error: ", ex);
            return BoolStoryValue.FALSE;
        }
    }
}
