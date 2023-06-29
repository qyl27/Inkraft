package cx.rain.mc.inkraft.story.function.game;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.story.function.StoryFunctionResults;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;

public class GiveItemFunction implements StoryFunction {
    @Override
    public String getName() {
        return "giveItem";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryFunctionResults.IStoryFunctionResult> func(boolean isDebug) {
        return (args, player) -> {
            if (args.length != 2) {
                return new StoryFunctionResults.BoolResult(false);
            }

            try {
                var id = args[0].toString();
                var count = Byte.parseByte(args[1].toString());
                var tag = new CompoundTag();
                tag.putString("id", id);
                tag.putByte("count", count);

                var itemStack = ItemStack.of(tag);
                giveItem(player, itemStack);

                return new StoryFunctionResults.BoolResult(true);
            } catch (RuntimeException ex) {
                ex.printStackTrace();

                return new StoryFunctionResults.BoolResult(false);
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
