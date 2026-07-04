package cx.rain.mc.inkraft.story.function.game.command.storage;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
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
        var server = instance.getPlayer().level().getServer();
        var id = StringArgumentParseHelper.parseId(args[0].getString());
        var storage = server.getCommandStorage();
        var tag = storage.get(id);

        try {
            var path = StringArgumentParseHelper.parseNbtPath(args[1].getString());
            var value = StringArgumentParseHelper.parseNbt(args[2].getString());
            path.set(tag, value);
            return BoolStoryValue.TRUE;
        } catch (CommandSyntaxException ex) {
            log.warn("NBT Path Error: ", ex);
            return BoolStoryValue.FALSE;
        }
    }
}
