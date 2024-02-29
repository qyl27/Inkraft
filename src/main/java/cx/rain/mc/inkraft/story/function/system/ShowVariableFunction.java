package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.story.PlayerStoryState;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.utility.StoryVariable;
import cx.rain.mc.inkraft.utility.ShowVariableHelper;

import java.util.function.Function;

public class ShowVariableFunction implements StoryFunction {

    @Override
    public String getName() {
        return "showVariable";
    }

    @Override
    public Function<Object[], StoryVariable.IValue> func(PlayerStoryState engine) {
        return args -> {
            if (args.length != 3) {
                return StoryVariable.BoolVar.FALSE;
            }

//            var name = args[0].toString();
//            var displayName = args[1].toString();
//            var isShow = args[2].toString().equalsIgnoreCase("true");
//            var value = InkraftPlatform.getPlayerStoryStateHolder(player).getVariable(name);

//            ShowVariableHelper.showVariable(player, name, displayName, isShow, value.asString());
//            engine.removeVariableObserver(name);
//
//            if (isShow) {
//                engine.observerVariable(name, ((variableName, newValue) -> {
//                    ShowVariableHelper.showVariable(player, variableName, displayName, true, newValue.toString());
//                }));
//            }

            return StoryVariable.BoolVar.TRUE;
        };
    }
}
