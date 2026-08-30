package cx.rain.mc.inkraft.story.function.system.flow;

import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;

public class IsInFlowFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "isInFlow";
    }

    @Override
    public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.expectCount(args, 1);

        var name = FunctionArgs.requireString(args[0]);
        return BoolStoryValue.from(instance.getRuntime().orElseThrow().getFlowName().equals(name));
    }
}
