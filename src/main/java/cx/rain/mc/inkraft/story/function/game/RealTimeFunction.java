package cx.rain.mc.inkraft.story.function.game;

import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class RealTimeFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "getRealTime";
    }

    @Override
    public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.expectCount(args, 1);

        var now = OffsetDateTime.now();
        var pattern = FunctionArgs.requireString(args[0]);
        var result = pattern.isEmpty() ? now.toString() : now.format(DateTimeFormatter.ofPattern(pattern));
        return IStoryValue.fromString(result);
    }
}
