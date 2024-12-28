package cx.rain.mc.inkraft.story.function.game.command.storage;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.utility.StringArgumentParseHelper;

public class GetStorageFunction implements IStoryFunction {

    @Override
    public String getName() {
        return "getStorage";
    }

    @Override
    public IStoryVariable<?> apply(StoryInstance instance, String... args) {
        var server = instance.getPlayer().getServer();
        assert server != null;
        var id = StringArgumentParseHelper.parseId(args[0]);
        var storage = server.getCommandStorage();
        var tag = storage.get(id);

        try {
            var path = StringArgumentParseHelper.parseNbtPath(args[1]);
            var list = path.get(tag);
            if (list.size() == 1) {
                return new IStoryVariable.Str(list.getFirst().getAsString());
            } else {
                return new IStoryVariable.Int(list.size());
            }
        } catch (CommandSyntaxException ex) {
            instance.getLogger().warn("NBT Path Error: ", ex);
        }

        return IStoryVariable.Bool.FALSE;
    }
}
