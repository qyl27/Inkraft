package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.story.StoryEngine;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.utility.StoryVariables;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class IsInStoryFunction implements StoryFunction {
    @Override
    public String getName() {
        return "isInStory";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryVariables.IStoryVariable> func(StoryEngine engine) {
        // Todo: qyl27: flow support.
        return (args, player) ->
                new StoryVariables.BoolVar(InkraftPlatform.getPlayerStoryStateHolder(player).isInStory());
    }
}
