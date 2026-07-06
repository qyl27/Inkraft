package cx.rain.mc.inkraft.story.function.game.command;

import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
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
    public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.requireCount(args, 1);
        FunctionArgs.requireTyped(args, 0, String.class);

        var player = instance.getPlayer();
        var scoreboard = player.level().getScoreboard();
        var objective = scoreboard.getObjective(FunctionArgs.getString(args[0]));
        if (objective == null) {
            return BoolStoryValue.FALSE;
        }

        var access = scoreboard.getOrCreatePlayerScore(player, objective);
        var result = function.apply(access);
        return new IntStoryValue(result);
    }

    public static ScoreboardFunction getScoreBoard() {
        return new ScoreboardFunction("getScoreboard", ScoreAccess::get);
    }
}
