package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.story.IStoryVariable;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class IsInStoryFunction implements StoryFunction {
    @Override
    public String getName() {
        return "isInStory";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, IStoryVariable.IStoryVariable> apply(StoryInstance engine, Object... args) {
        // Todo: qyl27: flow support.
        return (args, player) ->
                new IStoryVariable.Bool(InkraftPlatform.getPlayerStoryStateHolder(player).isInStory());
    }
}
