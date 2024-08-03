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

public class HasItemStackFunction implements StoryFunction {
    @Override
    public String getName() {
        return "hasItemStack";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, IStoryVariable.IStoryVariable> apply(StoryInstance engine, Object... args) {
        return (args, player) -> {
            if (args.length != 1) {
                return new IStoryVariable.Bool(false);
            }

            var item = args[0].toString();
            try {
                var itemStack = ItemStack.of(TagParser.parseTag(item));

                for (var i : player.getInventory().items) {
                    if (ItemStack.isSameItem(itemStack, i) && itemStack.getCount() <= i.getCount() && NbtMatchHelper.match(itemStack.getTag(), i.getTag())) {
                        return IStoryVariable.Bool.TRUE;
                    }
                }

                return IStoryVariable.Bool.FALSE;
            } catch (CommandSyntaxException ex) {
                ex.printStackTrace();

                return IStoryVariable.Bool.FALSE;
            }
        };
    }
}
