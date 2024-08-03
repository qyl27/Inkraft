package cx.rain.mc.inkraft.story.function.system.line;

import cx.rain.mc.inkraft.Constants;
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
        instance.getData().unsetVariable(Constants.Variables.LINE_PAUSE_TICKS);
        instance.getCancellationToken().cancel();
        return IStoryVariable.Bool.TRUE;
    }
}
