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
        FunctionArgs.expectCount(args, 2);

        var name = FunctionArgs.requireString(args[0]);
        var knot = FunctionArgs.requireString(args[1]);
        return BoolStoryValue.from(instance.requestNewFlow(name, knot));
    }
}
