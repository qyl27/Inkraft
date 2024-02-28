package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.mod.InkraftPlatform;
import cx.rain.mc.inkraft.story.PlayerStory;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.story.StoryVariables;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class IsInStoryFunction implements StoryFunction {
    @Override
    public String getName() {
        return "isInStory";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryVariables.IValue> func(PlayerStory engine) {
        // Todo: qyl27: flow support.
        return (args, player) ->
                new StoryVariables.BoolVar(InkraftPlatform.getPlayerStoryStateHolder(player).isInStory());
    }
}
