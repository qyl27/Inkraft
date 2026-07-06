package cx.rain.mc.inkraft.story.function.system.variable;

import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class HasVariableFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "hasVariable";
    }

    @Override
    public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.requireCount(args, 1);
        FunctionArgs.requireTyped(args, 0, String.class);

        var name = FunctionArgs.getString(args[0]);
        var result = instance.getData().hasVariable(name);
        return BoolStoryValue.from(result);
    }
}
