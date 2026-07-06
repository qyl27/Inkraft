package cx.rain.mc.inkraft.story.function.system.parse;

import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.utility.StringArgumentParseHelper;

public class ParseIntFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "parseInt";
    }

    @Override
    public IntStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.requireCount(args, 1);
        FunctionArgs.requireTyped(args, 0, String.class);

        var i = StringArgumentParseHelper.parseInt(FunctionArgs.getString(args[0]), 0);
        return new IntStoryValue(i);
    }
}
