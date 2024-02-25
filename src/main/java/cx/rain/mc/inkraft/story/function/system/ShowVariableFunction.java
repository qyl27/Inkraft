package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.story.StoryEngine;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.utility.StoryVariables;
import cx.rain.mc.inkraft.utility.ShowVariableHelper;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class ShowVariableFunction implements StoryFunction {

    @Override
    public String getName() {
        return "showVariable";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryVariables.IStoryVariable> func(StoryEngine engine) {
        return (args, player) -> {
            if (args.length != 3) {
                return StoryVariables.BoolVar.FALSE;
            }

            var name = args[0].toString();
            var displayName = args[1].toString();
            var isShow = args[2].toString().equalsIgnoreCase("true");
            var value = InkraftPlatform.getPlayerStoryStateHolder(player).getVariable(name);

            ShowVariableHelper.showVariable(player, name, displayName, isShow, value.asString());
            engine.removeVariableObserver(name);

            if (isShow) {
                engine.observerVariable(name, ((variableName, newValue) -> {
                    ShowVariableHelper.showVariable(player, variableName, displayName, true, newValue.toString());
                }));
            }

            return StoryVariables.BoolVar.TRUE;
        };
    }
}
