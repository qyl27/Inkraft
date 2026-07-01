package cx.rain.mc.inkraft.story.function.system.flow;

import cx.rain.mc.inkraft.story.StoryInstance;
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
        var name = args[0].getString();
        return BoolStoryValue.from(instance.getFlowName().equals(name));
    }
}
