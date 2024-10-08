package cx.rain.mc.inkraft.story.function.game.inventory;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.utility.ArgumentParseHelper;
import cx.rain.mc.inkraft.utility.ItemStackHelper;

public class HasItemFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "hasItem";
    }

    @Override
    public IStoryVariable<?> apply(StoryInstance instance, Object... args) {
        var player = instance.getPlayer();
        var registries = player.registryAccess();
        var predicate = ItemStackHelper.parsePredicate(registries, args, 0, 2);
        var count = ArgumentParseHelper.parseCount(args, 1);
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
