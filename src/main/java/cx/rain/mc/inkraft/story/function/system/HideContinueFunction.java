package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.story.PlayerStory;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.utility.StoryVariables;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class HideContinueFunction implements StoryFunction {
    @Override
    public String getName() {
        return "hideContinue";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryVariables.IValue> func(PlayerStory engine) {
        return (args, player) -> {
            if (args.length != 1) {
                return StoryVariables.BoolVar.FALSE;
            }

            var isHide = args[0].toString().equalsIgnoreCase("true");
            engine.setHideContinue(isHide);
            return StoryVariables.BoolVar.TRUE;
        };
    }
}
