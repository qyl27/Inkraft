package cx.rain.mc.inkraft.story.function.game.command;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.utility.StringArgumentParseHelper;
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
    public IStoryVariable<?> apply(StoryInstance instance, String... args) {
        var player = instance.getPlayer();
        var scoreboard = player.getScoreboard();
        var objective = scoreboard.getObjective(args[0]);
        if (objective == null) {
            return IStoryVariable.Bool.FALSE;
        }

        var value = StringArgumentParseHelper.parseInt(args[1], 0);
        var access = scoreboard.getOrCreatePlayerScore(player, objective);
        function.accept(access, value);
        return IStoryVariable.Bool.TRUE;
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
