package cx.rain.mc.inkraft.story.function.game.inventory;

import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.utility.ItemStackHelper;

public class CountItemFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "countItem";
    }

    @Override
    public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        var player = instance.getPlayer();
        var registries = player.registryAccess();
        var predicate = ItemStackHelper.createPredicate(
                registries, args[0].getString(), args[2].getString(), args[3].getString());
        var matched = ItemStackHelper.match(player, predicate);
        var result = 0;
        while (!matched.isEmpty()) {
            var s = matched.removeFirst();
            result += s.getCount();
        }
        return new IntStoryValue(result);
    }
}
