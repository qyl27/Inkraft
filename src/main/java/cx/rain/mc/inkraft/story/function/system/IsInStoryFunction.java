package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.story.PlayerStoryState;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.utility.StoryVariable;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class IsInStoryFunction implements StoryFunction {
    @Override
    public String getName() {
        return "isInStory";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryVariable.IValue> func(PlayerStoryState engine) {
        // Todo: qyl27: flow support.
        return (args, player) ->
                new StoryVariable.BoolVar(InkraftPlatform.getPlayerStoryHolder(player).isInStory());
    }
}
