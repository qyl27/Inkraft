package cx.rain.mc.inkraft.story.function;

import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class GetPlayerNameFunction implements StoryFunction {
    @Override
    public BiFunction<Object[], ServerPlayer, StoryFunctionResults.IStoryFunctionResult> func() {
        return (args, player) -> new StoryFunctionResults.StringResult(player.getName().getString());
    }

    @Override
    public String getName() {
        return "getPlayerName";
    }
}
