package cx.rain.mc.inkraft.story.function.system.flow;

import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class FlowToDefaultFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "flowToDefault";
    }

    @Override
    public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        instance.flowBackDefault();
        return BoolStoryValue.TRUE;
    }
}
