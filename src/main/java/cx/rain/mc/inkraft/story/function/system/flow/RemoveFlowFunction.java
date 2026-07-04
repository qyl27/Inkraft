package cx.rain.mc.inkraft.story.function.system.flow;

import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class RemoveFlowFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "removeFlow";
    }

    @Override
    public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        var name = args[0].getString();
        instance.removeFlow(name);
        return BoolStoryValue.TRUE;
    }
}
