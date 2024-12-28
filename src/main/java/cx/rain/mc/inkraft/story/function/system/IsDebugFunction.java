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
    public IStoryVariable.Bool apply(StoryInstance instance, String... args) {
        return IStoryVariable.Bool.from(instance.getManager().isDebug());
    }
}
