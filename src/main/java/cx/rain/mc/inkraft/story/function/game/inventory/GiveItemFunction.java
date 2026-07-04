package cx.rain.mc.inkraft.story.function.game.inventory;

import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.utility.StringArgumentParseHelper;
import cx.rain.mc.inkraft.utility.ItemStackHelper;

public class GiveItemFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "giveItem";
    }

    @Override
    public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        var player = instance.getPlayer();
        var registries = player.registryAccess();
        var item = ItemStackHelper.createItemStack(registries,
                args[0].getString(), args[1].getString(), args[2].getString(), args[3].getString());
        var result = player.addItem(item);
        return BoolStoryValue.from(result);
    }
}
