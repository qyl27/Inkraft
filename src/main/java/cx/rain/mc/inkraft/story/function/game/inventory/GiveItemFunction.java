package cx.rain.mc.inkraft.story.function.game.inventory;

import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.utility.ItemStackHelper;

public class GiveItemFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "giveItem";
    }

    @Override
    public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.requireCount(args, 4);
        FunctionArgs.requireTyped(args, 0, String.class);
        FunctionArgs.requireTyped(args, 2, String.class);
        FunctionArgs.requireTyped(args, 3, String.class);
        var count = FunctionArgs.getIntOrDefault(args[1], 1);

        var player = instance.getPlayer();
        var registries = player.registryAccess();
        var item = ItemStackHelper.createItemStack(registries,
                FunctionArgs.getString(args[0]), Integer.toString(count), FunctionArgs.getString(args[2]),
                FunctionArgs.getString(args[3]));
        var result = player.addItem(item);
        return BoolStoryValue.from(result);
    }
}
