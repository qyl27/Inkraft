package cx.rain.mc.inkraft.story.function.game;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.utility.NbtMatchHelper;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;

public class TakeItemStackFunction implements StoryFunction {
    @Override
    public String getName() {
        return "takeItemStack";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, IStoryVariable.IStoryVariable> apply(StoryInstance engine, Object... args) {
        return (args, player) -> {
            if (args.length != 1) {
                return new IStoryVariable.Int(0);
            }

            var item = args[0].toString();
            try {
                var itemStack = ItemStack.of(TagParser.parseTag(item));
                var count = takeItem(player, itemStack);
                return new IStoryVariable.Int(count);
            } catch (CommandSyntaxException ex) {
                ex.printStackTrace();

                return new IStoryVariable.Int(0);
            }
        };
    }

    private static int takeItem(ServerPlayer player, ItemStack stack) {
        var count = 0;

        for (ItemStack i : player.getInventory().items) {
            if (!i.isEmpty() && ItemStack.isSameItem(i, stack) && NbtMatchHelper.match(i.getTag(), stack.getTag())) {
                if (i.getCount() >= stack.getCount()) {
                    i.shrink(stack.getCount());
                    return stack.getCount();
                } else {
                    var c = i.getCount();
                    i.shrink(c);
                    stack.shrink(c);
                    count += c;
                }
            }
        }

        return count;
    }
}
