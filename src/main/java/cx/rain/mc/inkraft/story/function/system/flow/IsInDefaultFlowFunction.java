package cx.rain.mc.inkraft.story.function.system.flow;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.StoryFunction;

public class IsInDefaultFlowFunction implements StoryFunction {
    @Override
    public String getName() {
        return "isInDefaultFlow";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, Object... args) {
        return new IStoryVariable.Bool(instance.isDefaultFlow());
    }
}
