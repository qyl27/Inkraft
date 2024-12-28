package cx.rain.mc.inkraft.story.function.system.parse;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.utility.StringArgumentParseHelper;

public class ParseIntFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "parseInt";
    }

    @Override
    public IStoryVariable.Int apply(StoryInstance instance, String... args) {
        var i = StringArgumentParseHelper.parseInt(args[0], 0);
        return new IStoryVariable.Int(i);
    }
}
