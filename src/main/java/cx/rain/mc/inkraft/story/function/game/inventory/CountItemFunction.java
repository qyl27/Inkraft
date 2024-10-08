package cx.rain.mc.inkraft.story.function.game.inventory;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.utility.ItemStackHelper;

public class CountItemFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "countItem";
    }

    @Override
    public IStoryVariable<?> apply(StoryInstance instance, Object... args) {
        var player = instance.getPlayer();
        var registries = player.registryAccess();
        var predicate = ItemStackHelper.parsePredicate(registries, args, 0, 1);
        var matched = ItemStackHelper.match(player, predicate);
        var result = 0;
        while (!matched.isEmpty()) {
            var s = matched.removeFirst();
            result += s.getCount();
        }
        return new IStoryVariable.Int(result);
    }
}
