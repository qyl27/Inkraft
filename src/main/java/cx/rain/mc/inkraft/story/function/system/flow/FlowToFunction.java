package cx.rain.mc.inkraft.story.function.system.flow;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class FlowToFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "flowTo";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, String... args) {
        var name = args[0];
        instance.flowTo(name);
        return IStoryVariable.Bool.TRUE;
    }
}
