package cx.rain.mc.inkraft.story.function.game.inventory;

import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
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
    public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        var player = instance.getPlayer();
        var registries = player.registryAccess();
        var predicate = ItemStackHelper.createPredicate(
                registries, args[0].getString(), args[2].getString(), args[3].getString());
        var count = StringArgumentParseHelper.parseCount(args[1].getString());
        var result = ItemStackHelper.match(player, predicate);
        while (count > 0 && !result.isEmpty()) {
            var s = result.removeFirst();
            count -= s.getCount();
            if (count <= 0) {
                return BoolStoryValue.TRUE;
            }
        }
        return BoolStoryValue.FALSE;
    }
}
