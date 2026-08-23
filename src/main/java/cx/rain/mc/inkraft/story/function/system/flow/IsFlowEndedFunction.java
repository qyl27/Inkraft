package cx.rain.mc.inkraft.story.function.system.flow;

import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;

public class IsFlowEndedFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "isFlowEnded";
    }

    @Override
    public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.requireCount(args, 1);
        FunctionArgs.requireTyped(args, 0, String.class);

        var name = FunctionArgs.getString(args[0]);
        return BoolStoryValue.from(instance.getRuntime().orElseThrow().isFlowEnded(name));
    }
}
