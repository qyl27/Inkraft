package cx.rain.mc.inkraft.story.function.game;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cx.rain.mc.inkraft.story.PlayerStoryState;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.utility.StoryVariable;
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
    public BiFunction<Object[], ServerPlayer, StoryVariable.IValue> func(PlayerStoryState engine) {
        return (args, player) -> {
            if (args.length != 1) {
                return new StoryVariable.BoolVar(false);
            }

            var item = args[0].toString();
            try {
                var itemStack = ItemStack.of(TagParser.parseTag(item));

                for (var i : player.getInventory().items) {
                    if (ItemStack.isSameItem(itemStack, i) && itemStack.getCount() <= i.getCount() && NbtMatchHelper.match(itemStack.getTag(), i.getTag())) {
                        return StoryVariable.BoolVar.TRUE;
                    }
                }

                return StoryVariable.BoolVar.FALSE;
            } catch (CommandSyntaxException ex) {
                ex.printStackTrace();

                return StoryVariable.BoolVar.FALSE;
            }
        };
    }
}
