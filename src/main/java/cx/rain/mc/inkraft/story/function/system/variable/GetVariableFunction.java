package cx.rain.mc.inkraft.story.function.system.variable;

import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.story.IStoryVariable;

public class GetVariableFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "getVariable";
    }

    @Override
    public IStoryVariable<?> apply(StoryInstance instance, Object... args) {
        var name = args[0].toString();
        var v = instance.getData().getVariable(name);
        if (v == null) {
            return IStoryVariable.Bool.FALSE;
        }

        return v;
    }
}
