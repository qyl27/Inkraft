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

public class HasItemStackFunction implements StoryFunction {
    @Override
    public String getName() {
        return "hasItemStack";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryVariables.IStoryVariable> func(StoryEngine engine) {
        return (args, player) -> {
            if (args.length != 1) {
                return new StoryVariables.BoolVar(false);
            }

            var item = args[0].toString();
            try {
                var itemStack = ItemStack.of(TagParser.parseTag(item));

                for (var i : player.getInventory().items) {
                    if (ItemStack.isSameItem(itemStack, i) && NbtMatchHelper.match(itemStack.getTag(), i.getTag())) {
                        return StoryVariables.BoolVar.TRUE;
                    }
                }

                return StoryVariables.BoolVar.FALSE;
            } catch (CommandSyntaxException ex) {
                ex.printStackTrace();

                return StoryVariables.BoolVar.FALSE;
            }
        };
    }
}
