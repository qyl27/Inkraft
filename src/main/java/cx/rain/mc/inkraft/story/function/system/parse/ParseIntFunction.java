package cx.rain.mc.inkraft.story.function.system.parse;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class ParseIntFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "parseInt";
    }

    @Override
    public IStoryVariable.Int apply(StoryInstance instance, String... args) {
        var str = args[0];
        return new IStoryVariable.Int(Integer.parseInt(str));
    }
}
