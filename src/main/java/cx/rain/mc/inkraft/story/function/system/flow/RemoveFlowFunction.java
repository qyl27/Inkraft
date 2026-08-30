package cx.rain.mc.inkraft.story.function.system.flow;

import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class RemoveFlowFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "removeFlow";
    }

    @Override
    public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.expectCount(args, 1);

        var name = FunctionArgs.requireString(args[0]);
        return BoolStoryValue.from(instance.requestRemoveFlow(name));
    }
}
