package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.story.IStoryVariable;

public class IsDebugFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "isDebug";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, Object... args) {
        return new IStoryVariable.Bool(instance.getManager().isDebug());
    }
}
