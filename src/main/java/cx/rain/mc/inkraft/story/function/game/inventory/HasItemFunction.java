package cx.rain.mc.inkraft.story.function.game.inventory;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class HasItemFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "hasItem";
    }

    @Override
    public IStoryVariable<?> apply(StoryInstance instance, Object... args) {
        return null;
    }
}
