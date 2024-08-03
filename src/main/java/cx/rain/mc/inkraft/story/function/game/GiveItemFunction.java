package cx.rain.mc.inkraft.story.function.game;

import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.story.IStoryVariable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;

public class GiveItemFunction implements StoryFunction {
    @Override
    public String getName() {
        return "giveItem";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, IStoryVariable.IStoryVariable> apply(StoryInstance engine, Object... args) {
        return (args, player) -> {
            if (args.length != 2) {
                return new IStoryVariable.Bool(false);
            }

            try {
                var id = args[0].toString();
                var count = Byte.parseByte(args[1].toString());

                if (count < 1 || count > 64) {
                    return new IStoryVariable.Bool(false);
                }

                var tag = new CompoundTag();
                tag.putString("id", id);
                tag.putByte("count", count);

                var itemStack = ItemStack.of(tag);
                giveItem(player, itemStack);

                return new IStoryVariable.Bool(true);
            } catch (RuntimeException ex) {
                ex.printStackTrace();

                return new IStoryVariable.Bool(false);
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
