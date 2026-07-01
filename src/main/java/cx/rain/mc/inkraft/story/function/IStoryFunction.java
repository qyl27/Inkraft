package cx.rain.mc.inkraft.story.function;

import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.value.IStoryValue;

public interface IStoryFunction {
    String getName();

    IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args);

    default boolean isLookaheadSafe() {
        return false;
    }
}
