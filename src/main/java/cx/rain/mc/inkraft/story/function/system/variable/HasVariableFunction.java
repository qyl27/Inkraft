package cx.rain.mc.inkraft.story.function.system.variable;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class HasVariableFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "hasVariable";
    }

    @Override
    public IStoryVariable<?> apply(StoryInstance instance, String... args) {
        var name = args[0];
        var result = instance.getData().hasVariable(name);
        return IStoryVariable.Bool.from(result);
    }
}
