package cx.rain.mc.inkraft.story.function.system.line;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class IsEndedFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "isEnded";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, String... args) {
        return new IStoryVariable.Bool(instance.isStoryEnded());
    }
}
