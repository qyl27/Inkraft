package cx.rain.mc.inkraft.story.function.game;

import cx.rain.mc.inkraft.story.StoryEngine;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.story.function.StoryFunctionResults;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class GetPlayerNameFunction implements StoryFunction {
    @Override
    public String getName() {
        return "getPlayerName";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryFunctionResults.IStoryFunctionResult> func(StoryEngine engine) {
        return (args, player) -> new StoryFunctionResults.StringResult(player.getName().getString());
    }
}
