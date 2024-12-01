package cx.rain.mc.inkraft.story.function.game.inventory;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.utility.StringArgumentParseHelper;
import cx.rain.mc.inkraft.utility.ItemStackHelper;

public class TakeItemFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "takeItem";
    }

    @Override
    public IStoryVariable<?> apply(StoryInstance instance, Object... args) {
        var player = instance.getPlayer();
        var registries = player.registryAccess();
        var predicate = ItemStackHelper.createPredicate(registries, args[0].toString(), args[2].toString(), args[3].toString());
        var count = StringArgumentParseHelper.parseCount(args[1].toString());
        var took = player.getInventory().clearOrCountMatchingItems(predicate, count, player.getInventory());
        return new IStoryVariable.Bool(count == took);
    }
}
