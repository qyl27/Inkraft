package cx.rain.mc.inkraft.story.function.system.parse;

import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class ParseBoolFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "parseBool";
    }

    @Override
    public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.requireCount(args, 1);
        FunctionArgs.requireTyped(args, 0, String.class);

        var str = FunctionArgs.getString(args[0]);
        if ("true".equalsIgnoreCase(str)) {
            return BoolStoryValue.TRUE;
        }

        try {
            var f = Float.parseFloat(str);
            return BoolStoryValue.from(f != 0);
        } catch (NumberFormatException ignored) {
        }

        return BoolStoryValue.FALSE;
    }
}
