package cx.rain.mc.inkraft.story.function.system.logging;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class LogWarnFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "logWarn";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, Object... args) {
        var message = args[0].toString();
        Inkraft.getInstance().getLogger().warn(message);
        return IStoryVariable.Bool.TRUE;
    }
}
