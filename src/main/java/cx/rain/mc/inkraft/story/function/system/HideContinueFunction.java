package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.story.PlayerStoryState;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.utility.StoryVariable;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class HideContinueFunction implements StoryFunction {
    @Override
    public String getName() {
        return "hideContinue";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryVariable.IValue> func(PlayerStoryState engine) {
        return (args, player) -> {
            if (args.length != 1) {
                return StoryVariable.BoolVar.FALSE;
            }

            var isHide = args[0].toString().equalsIgnoreCase("true");
            engine.setHideContinue(isHide);
            return StoryVariable.BoolVar.TRUE;
        };
    }
}
