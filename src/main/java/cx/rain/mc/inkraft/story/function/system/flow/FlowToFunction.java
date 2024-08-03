package cx.rain.mc.inkraft.story.function.system.flow;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.StoryFunction;

public class FlowToFunction implements StoryFunction {
    @Override
    public String getName() {
        return "flowTo";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, Object... args) {
        if (args.length == 1) {
            var name = args[0].toString();
            instance.flowTo(name);
            return IStoryVariable.Bool.TRUE;
        } else if (args.length == 2) {
            var name = args[0].toString();
            var knot = args[1].toString();
            instance.addFlow(name, knot);
            return IStoryVariable.Bool.TRUE;
        }
        return IStoryVariable.Bool.FALSE;
    }
}
