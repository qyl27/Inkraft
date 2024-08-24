package cx.rain.mc.inkraft.story.function.system.variable;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.StoryFunction;

public class HasVariableFunction implements StoryFunction {
    @Override
    public String getName() {
        return "hasVariable";
    }

    @Override
    public IStoryVariable<?> apply(StoryInstance instance, Object... args) {
        var name = args[0].toString();
        var result = instance.getData().hasVariable(name);
        return new IStoryVariable.Bool(result);
    }
}
