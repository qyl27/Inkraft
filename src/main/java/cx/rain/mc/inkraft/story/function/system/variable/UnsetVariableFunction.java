package cx.rain.mc.inkraft.story.function.system.variable;

import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class UnsetVariableFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "unsetVariable";
    }

    @Override
    public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.requireCount(args, 1);
        FunctionArgs.requireTyped(args, 0, String.class);

        var name = FunctionArgs.getString(args[0]);
        instance.getData().unsetVariable(name);
        return BoolStoryValue.TRUE;
    }
}
