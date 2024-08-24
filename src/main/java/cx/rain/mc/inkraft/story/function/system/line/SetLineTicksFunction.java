package cx.rain.mc.inkraft.story.function.system.line;

import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class SetLineTicksFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "setLineTicks";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, Object... args) {
        var ticks = Integer.parseInt(args[0].toString());
        instance.getData().setVariable(ModConstants.Variables.LINE_PAUSE_TICKS, new IStoryVariable.Int(ticks));
        instance.stop(false);
        instance.start();
        return IStoryVariable.Bool.TRUE;
    }
}
