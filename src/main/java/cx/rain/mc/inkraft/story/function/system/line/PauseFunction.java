package cx.rain.mc.inkraft.story.function.system.line;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class PauseFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, Object... args) {
        instance.stop(true);
        return IStoryVariable.Bool.TRUE;
    }
}
