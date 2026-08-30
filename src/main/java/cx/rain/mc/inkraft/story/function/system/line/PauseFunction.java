package cx.rain.mc.inkraft.story.function.system.line;

import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class PauseFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.expectCount(args, 0);

        instance.requestPause();
        return BoolStoryValue.TRUE;
    }
}
