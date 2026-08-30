package cx.rain.mc.inkraft.story.function.system.variable;

import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;

public class GetVariableFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "getVariable";
    }

    @Override
    public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.expectCount(args, 1);

        var name = FunctionArgs.requireString(args[0]);
        var v = instance.getData().getVariable(name);
        if (v == null) {
            return BoolStoryValue.FALSE;
        }

        return v;
    }
}
