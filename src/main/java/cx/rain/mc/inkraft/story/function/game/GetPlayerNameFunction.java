package cx.rain.mc.inkraft.story.function.game;

import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.story.IStoryVariable;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class GetPlayerNameFunction implements StoryFunction {
    @Override
    public String getName() {
        return "getPlayerName";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, IStoryVariable.IStoryVariable> apply(StoryInstance engine, Object... args) {
        return (args, player) -> new IStoryVariable.Str(player.getName().getString());
    }
}
