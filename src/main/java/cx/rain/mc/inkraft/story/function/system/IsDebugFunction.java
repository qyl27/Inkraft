package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;

public class IsDebugFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "isDebug";
    }

    @Override
    public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.requireCount(args, 0);

        return BoolStoryValue.from(instance.getManager().isDebug());
    }
}
