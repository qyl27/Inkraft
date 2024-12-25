package cx.rain.mc.inkraft.story.function.system.variable;

import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.story.IStoryVariable;

public class SetVariableFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "setVariable";
    }

    @Override
    public IStoryVariable.Bool apply(StoryInstance instance, String... args) {
        var name = args[0];
        var value = args[1];
        instance.getData().setVariable(name, IStoryVariable.fromString(value));
        return IStoryVariable.Bool.TRUE;
    }
}
