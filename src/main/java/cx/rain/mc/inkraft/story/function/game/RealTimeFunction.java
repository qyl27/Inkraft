package cx.rain.mc.inkraft.story.function.game;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class RealTimeFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "getRealTime";
    }

    @Override
    public IStoryVariable<?> apply(StoryInstance instance, Object... args) {
        var now = OffsetDateTime.now();
        if (args.length == 1) {
            var pattern = args[0].toString();
            return new IStoryVariable.Str(now.format(DateTimeFormatter.ofPattern(pattern)));
        } else {
            return new IStoryVariable.Str(now.toString());
        }
    }
}
