package cx.rain.mc.inkraft.story.function.system.flow;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class NewFlowFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "newFlow";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, Object... args) {
        var name = args[0].toString();
        var knot = args[1].toString();
        instance.addFlow(name, knot);
        return IStoryVariable.Bool.TRUE;
    }
}
