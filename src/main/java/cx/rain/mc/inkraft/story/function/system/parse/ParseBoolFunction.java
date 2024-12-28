package cx.rain.mc.inkraft.story.function.system.parse;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class ParseBoolFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "parseBool";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, String... args) {
        var str = args[0];
        if ("true".equalsIgnoreCase(str)) {
            return IStoryVariable.Bool.TRUE;
        }

        try {
            var f = Float.parseFloat(str);
            return IStoryVariable.Bool.from(f != 0);
        } catch (NumberFormatException ignored) {
        }

        return IStoryVariable.Bool.FALSE;
    }
}
