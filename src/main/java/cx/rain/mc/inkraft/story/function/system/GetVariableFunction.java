package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.story.IStoryVariable;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class GetVariableFunction implements StoryFunction {
    @Override
    public String getName() {
        return "getVariable";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, IStoryVariable.IStoryVariable> apply(StoryInstance engine, Object... args) {
        return (args, player) -> {
            if (args.length != 1) {
                return IStoryVariable.Bool.FALSE;
            }

            var name = args[0].toString();
            return InkraftPlatform.getPlayerStoryStateHolder(player).getVariable(name);
        };
    }
}
