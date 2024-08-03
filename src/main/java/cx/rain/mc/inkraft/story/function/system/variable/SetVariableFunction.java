package cx.rain.mc.inkraft.story.function.system.variable;

import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.story.IStoryVariable;

public class SetVariableFunction implements StoryFunction {
    @Override
    public String getName() {
        return "setVariable";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, Object... args) {
        var name = args[0].toString();
        var value = args[1].toString();
        instance.getData().setVariable(name, IStoryVariable.fromString(value));
        return IStoryVariable.Bool.TRUE;
    }
}
