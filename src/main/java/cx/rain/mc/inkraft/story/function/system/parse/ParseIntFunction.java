package cx.rain.mc.inkraft.story.function.system.parse;

import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class ParseIntFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "parseInt";
    }

    @Override
    public IntStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.expectCount(args, 1);
        var str = args[0].getString();

        try {
            var i = Integer.parseInt(str);
            return new IntStoryValue(i);
        } catch (NumberFormatException ignored) {
        }

        return new IntStoryValue(0);
    }
}
