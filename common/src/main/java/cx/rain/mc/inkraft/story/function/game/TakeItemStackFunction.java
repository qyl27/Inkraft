package cx.rain.mc.inkraft.story.function.game;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cx.rain.mc.inkraft.story.StoryEngine;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.utility.StoryVariables;
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
    public BiFunction<Object[], ServerPlayer, StoryVariables.IStoryVariable> func(StoryEngine engine) {
        return (args, player) -> {
            if (args.length != 1) {
                return new StoryVariables.IntVar(0);
            }

            var item = args[0].toString();
            try {
                var itemStack = ItemStack.of(TagParser.parseTag(item));
                return new StoryVariables.IntVar(takeItem(player, itemStack));
            } catch (CommandSyntaxException ex) {
                ex.printStackTrace();

                return new StoryVariables.IntVar(0);
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
