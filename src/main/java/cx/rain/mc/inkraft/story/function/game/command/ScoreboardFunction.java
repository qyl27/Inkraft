package cx.rain.mc.inkraft.story.function.game.command;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import net.minecraft.world.scores.ScoreAccess;

import java.util.function.Function;

public class ScoreboardFunction implements IStoryFunction {

    private final String name;
    private final Function<ScoreAccess, Integer> function;

    public ScoreboardFunction(String name, Function<ScoreAccess, Integer> function) {
        this.name = name;
        this.function = function;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IStoryVariable<?> apply(StoryInstance instance, String... args) {
        var player = instance.getPlayer();
        var scoreboard = player.getScoreboard();
        var objective = scoreboard.getObjective(args[0]);
        if (objective == null) {
            return IStoryVariable.Bool.FALSE;
        }

        var access = scoreboard.getOrCreatePlayerScore(player, objective);
        var result = function.apply(access);
        return new IStoryVariable.Int(result);
    }

    public static ScoreboardFunction getScoreBoard() {
        return new ScoreboardFunction("getScoreboard", ScoreAccess::get);
    }
}
