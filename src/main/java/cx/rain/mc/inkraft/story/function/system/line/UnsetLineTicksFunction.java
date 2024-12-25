package cx.rain.mc.inkraft.story.function.system.line;

import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class UnsetLineTicksFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "unsetLineTicks";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, String... args) {
        instance.getData().unsetVariable(ModConstants.Variables.LINE_PAUSE_TICKS);
        instance.stop(false);
        instance.start();
        return IStoryVariable.Bool.TRUE;
    }
}
