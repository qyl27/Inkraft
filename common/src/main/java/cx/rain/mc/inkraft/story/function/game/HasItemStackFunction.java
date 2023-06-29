package cx.rain.mc.inkraft.story.function.game;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.story.function.StoryFunctionResults;
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
    public BiFunction<Object[], ServerPlayer, StoryFunctionResults.IStoryFunctionResult> func(boolean isDebug) {
        return (args, player) -> {
            if (args.length != 1) {
                return new StoryFunctionResults.BoolResult(false);
            }

            var item = args[0].toString();
            try {
                var itemStack = ItemStack.of(TagParser.parseTag(item));
                return new StoryFunctionResults.BoolResult(player.getInventory().contains(itemStack));
            } catch (CommandSyntaxException ex) {
                ex.printStackTrace();

                return new StoryFunctionResults.BoolResult(false);
            }
        };
    }
}
