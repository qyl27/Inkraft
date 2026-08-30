package cx.rain.mc.inkraft.story.function.game.command.storage;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.FloatStoryValue;
import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.utility.StringArgumentParseHelper;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.nbt.NumericTag;

@Slf4j
public class GetStorageFunction implements IStoryFunction {

    @Override
    public String getName() {
        return "getStorage";
    }

    @Override
    public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.expectCount(args, 2);
        var idArgument = FunctionArgs.requireString(args[0]);
        var pathArgument = FunctionArgs.requireString(args[1]);

        var server = instance.getPlayer().level().getServer();
        var id = StringArgumentParseHelper.parseId(idArgument);
        var storage = server.getCommandStorage();
        var tag = storage.get(id);

        try {
            var path = StringArgumentParseHelper.parseNbtPath(pathArgument);
            var list = path.get(tag);
            if (list.size() == 1) {
                var t = list.getFirst();
                if (t instanceof NumericTag n) {
                    return new FloatStoryValue(n.floatValue());
                }
                return IStoryValue.fromString(t.asString().orElseGet(t::toString));
            } else {
                return new IntStoryValue(list.size());
            }
        } catch (CommandSyntaxException ex) {
            log.warn("NBT Path Error: ", ex);
        }

        return BoolStoryValue.FALSE;
    }
}
