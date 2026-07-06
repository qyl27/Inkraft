package cx.rain.mc.inkraft.story.function.system.variable;

import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;

public class SetVariableFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "setVariable";
    }

    @Override
    public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.requireCount(args, 2);
        FunctionArgs.requireTyped(args, 0, String.class);

        var name = FunctionArgs.getString(args[0]);
        var value = args[1];
        instance.getData().setVariable(name, value);
        return BoolStoryValue.TRUE;
    }
}
