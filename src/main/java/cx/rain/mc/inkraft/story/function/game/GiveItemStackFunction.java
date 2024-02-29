package cx.rain.mc.inkraft.story.function.game;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cx.rain.mc.inkraft.story.PlayerStoryState;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.utility.StoryVariable;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;

public class GiveItemStackFunction implements StoryFunction {
    @Override
    public String getName() {
        return "giveItemStack";
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
                giveItem(player, itemStack);

                return new StoryVariable.BoolVar(true);
            } catch (CommandSyntaxException ex) {
                ex.printStackTrace();

                return new StoryVariable.BoolVar(false);
            }
        };
    }

    private static void giveItem(ServerPlayer player, ItemStack stack) {
        var result = player.getInventory().add(stack);
        if (!result || !stack.isEmpty()) {
            var itemEntity = player.drop(stack, false);
            if (itemEntity != null) {
                itemEntity.setNoPickUpDelay();
                itemEntity.setTarget(player.getUUID());
            }
        }
    }
}
