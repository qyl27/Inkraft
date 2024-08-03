package cx.rain.mc.inkraft.story.function;

import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.IStoryVariable;

public interface StoryFunction {
    String getName();
    IStoryVariable apply(StoryInstance instance, Object... args);
}
