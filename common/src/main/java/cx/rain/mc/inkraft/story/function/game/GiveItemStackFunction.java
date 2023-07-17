package cx.rain.mc.inkraft.story.function.game;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cx.rain.mc.inkraft.story.StoryEngine;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.story.function.StoryFunctionResults;
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
    public BiFunction<Object[], ServerPlayer, StoryFunctionResults.IStoryFunctionResult> func(StoryEngine engine) {
        return (args, player) -> {
            if (args.length != 1) {
                return new StoryFunctionResults.BoolResult(false);
            }

            var item = args[0];
            try {
                var itemStack = ItemStack.of(TagParser.parseTag(item.toString()));
                giveItem(player, itemStack);

                return new StoryFunctionResults.BoolResult(true);
            } catch (CommandSyntaxException ex) {
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
