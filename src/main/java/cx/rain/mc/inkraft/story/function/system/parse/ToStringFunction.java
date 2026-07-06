package cx.rain.mc.inkraft.story.function.system.parse;

import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.value.StringStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class ToStringFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "toString";
    }

    @Override
    public StringStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.requireCount(args, 1);

        return new StringStoryValue(args[0].getString());
    }
}
