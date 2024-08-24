package cx.rain.mc.inkraft.story.function.system.variable;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class UnsetVariableFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "unsetVariable";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, Object... args) {
        var name = args[0].toString();
        instance.getData().unsetVariable(name);
        return IStoryVariable.Bool.TRUE;
    }
}
