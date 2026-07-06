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
        FunctionArgs.requireCount(args, 4);
        FunctionArgs.requireTyped(args, 0, String.class);
        FunctionArgs.requireTyped(args, 2, String.class);
        FunctionArgs.requireTyped(args, 3, String.class);

        var player = instance.getPlayer();
        var registries = player.registryAccess();
        var predicate = ItemStackHelper.createPredicate(
                registries, FunctionArgs.getString(args[0]), FunctionArgs.getString(args[2]),
                FunctionArgs.getString(args[3]));
        var matched = ItemStackHelper.match(player, predicate);
        var result = 0;
        while (!matched.isEmpty()) {
            var s = matched.removeFirst();
            result += s.getCount();
        }
        return new IntStoryValue(result);
    }
}
