package cx.rain.mc.inkraft.story.function.system.line;

import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class SetLineTicksFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "setLineTicks";
    }

    @Override
    public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.expectCount(args, 1);

        var ticks = FunctionArgs.requireInt(args[0]);
        if (ticks < -1) {
            return BoolStoryValue.FALSE;
        }

        instance.getData().setVariable(ModConstants.Variables.LINE_PAUSE_TICKS, new IntStoryValue(ticks));
        return BoolStoryValue.TRUE;
    }
}
