package cx.rain.mc.inkraft.story.function.system.line;

import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class IsEndedFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "isEnded";
    }

    @Override
    public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        return BoolStoryValue.from(instance.isStoryEnded());
    }
}
