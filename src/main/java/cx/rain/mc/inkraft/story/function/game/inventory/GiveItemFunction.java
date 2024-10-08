package cx.rain.mc.inkraft.story.function.game.inventory;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.utility.ArgumentParseHelper;
import cx.rain.mc.inkraft.utility.ItemStackHelper;

public class GiveItemFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "giveItem";
    }

    @Override
    public IStoryVariable<?> apply(StoryInstance instance, Object... args) {
        var player = instance.getPlayer();
        var registries = player.registryAccess();
        var item = ItemStackHelper.parseItemStack(registries, args, 0);
        var count = ArgumentParseHelper.parseCount(args, 1);
        item.setCount(count);
        var result = player.addItem(item);
        return new IStoryVariable.Bool(result);
    }
}
