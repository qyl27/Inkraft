package cx.rain.mc.inkraft.story.function.game;

import cx.rain.mc.inkraft.story.PlayerStory;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.utility.StoryVariables;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class GetPlayerNameFunction implements StoryFunction {
    @Override
    public String getName() {
        return "getPlayerName";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryVariables.IValue> func(PlayerStory engine) {
        return (args, player) -> new StoryVariables.StrVar(player.getName().getString());
    }
}
