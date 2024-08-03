package cx.rain.mc.inkraft.story.function.system.line;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.StoryFunction;

public class PauseFunction implements StoryFunction {
    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, Object... args) {
        instance.getCancellationToken().cancel();
        return IStoryVariable.Bool.TRUE;
    }
}
