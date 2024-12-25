package cx.rain.mc.inkraft.story.function.system.flow;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class FlowToDefaultFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "flowToDefault";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, String... args) {
        instance.flowBackDefault();
        return IStoryVariable.Bool.TRUE;
    }
}
