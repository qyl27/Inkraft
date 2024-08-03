package cx.rain.mc.inkraft.story.function.system.variable;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.StoryFunction;

public class ClearVariableFunction implements StoryFunction {
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
