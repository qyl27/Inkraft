package cx.rain.mc.inkraft.story.function.system.variable;

import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class UnsetVariableFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "unsetVariable";
    }

    @Override
    public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        var name = args[0].getString();
        instance.getData().unsetVariable(name);
        return BoolStoryValue.TRUE;
    }
}
