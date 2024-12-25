package cx.rain.mc.inkraft.story.function.game.inventory;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.utility.StringArgumentParseHelper;
import cx.rain.mc.inkraft.utility.ItemStackHelper;

public class HasItemFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "hasItem";
    }

    @Override
    public IStoryVariable<?> apply(StoryInstance instance, String... args) {
        var player = instance.getPlayer();
        var registries = player.registryAccess();
        var predicate = ItemStackHelper.createPredicate(registries, args[0], args[2], args[3]);
        var count = StringArgumentParseHelper.parseCount(args[1]);
        var result = ItemStackHelper.match(player, predicate);
        while (count > 0 && !result.isEmpty()) {
            var s = result.removeFirst();
            count -= s.getCount();
            if (count <= 0) {
                return IStoryVariable.Bool.TRUE;
            }
        }
        return IStoryVariable.Bool.FALSE;
    }
}
