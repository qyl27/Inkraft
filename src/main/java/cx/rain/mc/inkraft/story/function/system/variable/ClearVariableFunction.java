package cx.rain.mc.inkraft.story.function.system.variable;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class ClearVariableFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "clearVariables";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, Object... args) {
        instance.getData().clearVariables();
        return IStoryVariable.Bool.TRUE;
    }
}
