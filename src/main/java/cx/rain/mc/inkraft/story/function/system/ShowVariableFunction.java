package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.utility.ShowVariableHelper;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class ShowVariableFunction implements StoryFunction {

    @Override
    public String getName() {
        return "showVariable";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, IStoryVariable.IStoryVariable> apply(StoryInstance engine, Object... args) {
        return (args, player) -> {
            if (args.length != 3) {
                return IStoryVariable.Bool.FALSE;
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

            return IStoryVariable.Bool.TRUE;
        };
    }
}
