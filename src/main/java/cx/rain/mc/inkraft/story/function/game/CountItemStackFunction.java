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

public class CountItemStackFunction implements StoryFunction {
    @Override
    public String getName() {
        return "countItemStack";
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
                var count = 0;

                for (var i : player.getInventory().items) {
                    if (!i.isEmpty() && ItemStack.isSameItem(i, itemStack) && NbtMatchHelper.match(i.getTag(), itemStack.getTag())) {
                        count += i.getCount();
                    }
                }

                return new StoryVariables.IntVar(count);
            } catch (CommandSyntaxException ex) {
                ex.printStackTrace();

                return new StoryVariables.IntVar(0);
            }
        };
    }
}
