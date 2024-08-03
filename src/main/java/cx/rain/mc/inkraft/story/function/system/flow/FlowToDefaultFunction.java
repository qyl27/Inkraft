package cx.rain.mc.inkraft.story.function.system.flow;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.StoryFunction;

public class FlowToDefaultFunction implements StoryFunction {
    @Override
    public String getName() {
        return "flowToDefault";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, Object... args) {
        instance.flowBackDefault();
        return IStoryVariable.Bool.TRUE;
    }
}
