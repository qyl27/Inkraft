package cx.rain.mc.inkraft.story.function.system.parse;

import cx.rain.mc.inkraft.story.value.FloatStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.utility.StringArgumentParseHelper;

public class ParseFloatFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "parseFloat";
    }

    @Override
    public FloatStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        var f = StringArgumentParseHelper.parseFloat(args[0].getString(), 0);
        return new FloatStoryValue(f);
    }
}
