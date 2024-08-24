package cx.rain.mc.inkraft.story.function.system.flow;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class RemoveFlowFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "removeFlow";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, Object... args) {
        var name = args[0].toString();
        instance.removeFlow(name);
        return IStoryVariable.Bool.TRUE;
    }
}
