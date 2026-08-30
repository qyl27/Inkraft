package cx.rain.mc.inkraft.story.function.game.inventory;

import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.utility.ItemStackHelper;

public class CountItemFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "countItem";
    }

    @Override
    public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.expectCount(args, 3);
        var item = FunctionArgs.requireString(args[0]);
        var components = FunctionArgs.requireString(args[1]);
        var nbt = FunctionArgs.requireString(args[2]);

        var player = instance.getPlayer();
        var registries = player.registryAccess();
        var predicate = ItemStackHelper.createPredicate(registries, item, components, nbt);
        var matched = ItemStackHelper.match(player, predicate);
        var result = 0;
        while (!matched.isEmpty()) {
            var s = matched.removeFirst();
            result += s.getCount();
        }
        return new IntStoryValue(result);
    }
}
