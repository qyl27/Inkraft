package cx.rain.mc.inkraft.story.function.system.parse;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class ToStringFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "toString";
    }

    @Override
    public IStoryVariable.Str apply(StoryInstance instance, String... args) {
        return new IStoryVariable.Str(args[0]);
    }
}
