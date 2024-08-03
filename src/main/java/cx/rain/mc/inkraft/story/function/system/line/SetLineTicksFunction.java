package cx.rain.mc.inkraft.story.function.system.line;

import cx.rain.mc.inkraft.Constants;
import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.StoryFunction;

public class SetLineTicksFunction implements StoryFunction {
    @Override
    public String getName() {
        return "setLineTicks";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, Object... args) {
        var ticks = Integer.parseInt(args[0].toString());
        instance.getData().setVariable(Constants.Variables.LINE_PAUSE_TICKS, new IStoryVariable.Int(ticks));
        instance.getCancellationToken().cancel();
        return IStoryVariable.Bool.TRUE;
    }
}