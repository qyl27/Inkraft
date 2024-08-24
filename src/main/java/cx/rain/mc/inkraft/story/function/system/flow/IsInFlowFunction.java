package cx.rain.mc.inkraft.story.function.system.flow;

import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.story.IStoryVariable;

public class IsInFlowFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "isInFlow";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, Object... args) {
        var name = args[0].toString();
        return new IStoryVariable.Bool(instance.getFlowName().equals(name));
    }
}
