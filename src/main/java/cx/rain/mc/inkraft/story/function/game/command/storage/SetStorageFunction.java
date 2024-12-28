package cx.rain.mc.inkraft.story.function.game.command.storage;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.utility.StringArgumentParseHelper;
import net.minecraft.nbt.*;

public class SetStorageFunction implements IStoryFunction {

    @Override
    public String getName() {
        return "setStorage";
    }

    @Override
    public IStoryVariable<?> apply(StoryInstance instance, String... args) {
        var server = instance.getPlayer().getServer();
        var id = StringArgumentParseHelper.parseId(args[0]);
        var path = StringArgumentParseHelper.parseNbtPath(args[1]);
        var type = StorageType.from(args[2]);

        assert server != null;
        var storage = server.getCommandStorage();
        var tag = storage.get(id);

        var valueArg = args[3];
        Tag t = switch (type) {
            case BYTE -> ByteTag.valueOf(StringArgumentParseHelper.parseByte(valueArg, (byte) 0));
            case SHORT -> ShortTag.valueOf(StringArgumentParseHelper.parseShort(valueArg, (short) 0));
            case INT -> IntTag.valueOf(StringArgumentParseHelper.parseInt(valueArg, 0));
            case LONG -> LongTag.valueOf(StringArgumentParseHelper.parseLong(valueArg, 0));
            case FLOAT -> FloatTag.valueOf(StringArgumentParseHelper.parseFloat(valueArg, 0));
            case DOUBLE -> DoubleTag.valueOf(StringArgumentParseHelper.parseDouble(valueArg, 0));
        };

        try {
            path.set(tag, t);
            return IStoryVariable.Bool.TRUE;
        } catch (CommandSyntaxException ex) {
            instance.getLogger().warn("NBT Path Error: ", ex);
            return IStoryVariable.Bool.FALSE;
        }
    }
}
