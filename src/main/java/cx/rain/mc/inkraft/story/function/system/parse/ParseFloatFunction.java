package cx.rain.mc.inkraft.story.function.system.parse;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class ParseFloatFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "parseFloat";
    }

    @Override
    public IStoryVariable.Float apply(StoryInstance instance, String... args) {
        var str = args[0];
        return new IStoryVariable.Float(Float.parseFloat(str));
    }
}
