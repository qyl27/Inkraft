package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.story.function.StoryFunctionResults;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class IsInStoryFunction implements StoryFunction {
    @Override
    public String getName() {
        return "isInStory";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryFunctionResults.IStoryFunctionResult> func(boolean isDebug) {
        // Todo: qyl27: flow support.
        return (args, player) ->
                new StoryFunctionResults.BoolResult(InkraftPlatform.getPlayerStoryStateHolder(player).isInStory());
    }
}
