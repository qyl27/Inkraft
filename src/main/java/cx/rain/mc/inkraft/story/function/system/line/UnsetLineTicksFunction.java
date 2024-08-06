package cx.rain.mc.inkraft.story.function.system.line;

import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.StoryFunction;

public class UnsetLineTicksFunction implements StoryFunction {
    @Override
    public String getName() {
        return "unsetLineTicks";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, Object... args) {
        instance.getData().unsetVariable(ModConstants.Variables.LINE_PAUSE_TICKS);
        instance.stop();
        return IStoryVariable.Bool.TRUE;
    }
}
