package cx.rain.mc.inkraft.story.function.system.line;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.StoryFunction;

public class IsEndedFunction implements StoryFunction {
    @Override
    public String getName() {
        return "isEnded";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, Object... args) {
        return new IStoryVariable.Bool(instance.isStoryEnded());
    }
}
