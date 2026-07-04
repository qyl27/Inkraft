package cx.rain.mc.inkraft.story.function.system.parse;

import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.utility.StringArgumentParseHelper;

public class ParseIntFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "parseInt";
    }

    @Override
    public IntStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        var i = StringArgumentParseHelper.parseInt(args[0].getString(), 0);
        return new IntStoryValue(i);
    }
}
