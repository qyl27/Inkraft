package cx.rain.mc.inkraft.story.function.system.flow;

import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class NewFlowFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "newFlow";
    }

    @Override
    public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.requireCount(args, 2);
        FunctionArgs.requireTyped(args, 0, String.class);
        FunctionArgs.requireTyped(args, 1, String.class);

        var name = FunctionArgs.getString(args[0]);
        var knot = FunctionArgs.getString(args[1]);
        return BoolStoryValue.from(instance.requestNewFlow(name, knot));
    }
}
