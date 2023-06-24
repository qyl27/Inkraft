package cx.rain.mc.inkraft.story.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;

public class GiveItemCommand implements StoryCommand {
    @Override
    public String getName() {
        return "GIVE";
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public BiConsumer<String[], ServerPlayer> getConsumer() {
        return (args, player) -> {
            if (args.length != 1) {
                return;
            }

            var item = args[0];
            try {
                var itemStack = ItemStack.of(TagParser.parseTag(item));
                giveItem(player, itemStack);
            } catch (CommandSyntaxException ex) {
                ex.printStackTrace();
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
