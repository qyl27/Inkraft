package cx.rain.mc.inkraft.story.function.system.line;

import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class UnsetLineTicksFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "unsetLineTicks";
    }

    @Override
    public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.requireCount(args, 0);

        instance.getData().unsetVariable(ModConstants.Variables.LINE_PAUSE_TICKS);
        return BoolStoryValue.TRUE;
    }
}
