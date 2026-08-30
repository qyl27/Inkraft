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
        FunctionArgs.expectCount(args, 4);
        var item = FunctionArgs.requireString(args[0]);
        var components = FunctionArgs.requireString(args[2]);
        var nbt = FunctionArgs.requireString(args[3]);
        var count = FunctionArgs.getInt(args[1]).orElse(1);

        var player = instance.getPlayer();
        var registries = player.registryAccess();
        var stack = ItemStackHelper.createItemStack(registries, item, Integer.toString(count), components, nbt);
        var result = player.addItem(stack);
        return BoolStoryValue.from(result);
    }
}
