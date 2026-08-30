package cx.rain.mc.inkraft.story.function.game.command;

import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import net.minecraft.world.scores.ScoreAccess;

import java.util.function.BiConsumer;

public class ScoreboardValuedFunction implements IStoryFunction {

    private final String name;
    private final BiConsumer<ScoreAccess, Integer> function;

    public ScoreboardValuedFunction(String name, BiConsumer<ScoreAccess, Integer> function) {
        this.name = name;
        this.function = function;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.expectCount(args, 2);
        var objectiveName = FunctionArgs.requireString(args[0]);
        var value = FunctionArgs.requireInt(args[1]);

        var player = instance.getPlayer();
        var scoreboard = player.level().getScoreboard();
        var objective = scoreboard.getObjective(objectiveName);
        if (objective == null) {
            return BoolStoryValue.FALSE;
        }

        var access = scoreboard.getOrCreatePlayerScore(player, objective);
        function.accept(access, value);
        return BoolStoryValue.TRUE;
    }

    public static ScoreboardValuedFunction setScoreBoard() {
        return new ScoreboardValuedFunction("setScoreboard", ScoreAccess::set);
    }

    public static ScoreboardValuedFunction addScoreBoard() {
        return new ScoreboardValuedFunction("addScoreboard", ScoreAccess::add);
    }

    public static ScoreboardValuedFunction subScoreBoard() {
        return new ScoreboardValuedFunction("subScoreboard", (score, value) -> score.set(Math.max(score.get() - value, 0)));
    }

    public static ScoreboardValuedFunction multiplyScoreBoard() {
        return new ScoreboardValuedFunction("multiplyScoreboard", (score, value) -> score.set(score.get() * value));
    }
}
