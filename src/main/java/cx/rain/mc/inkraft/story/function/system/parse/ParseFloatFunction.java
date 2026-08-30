package cx.rain.mc.inkraft.story.function.system.parse;

import cx.rain.mc.inkraft.story.value.FloatStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class ParseFloatFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "parseFloat";
    }

    @Override
    public FloatStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.expectCount(args, 1);
        var str = args[0].getString();

        try {
            var f = Float.parseFloat(str);
            return new FloatStoryValue(f);
        } catch (NumberFormatException ignored) {
        }

        return new FloatStoryValue(0);
    }
}
