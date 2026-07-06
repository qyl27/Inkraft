package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.function.BiConsumer;

@Slf4j
public class LogFunction implements IStoryFunction {
    private final String name;
    private final BiConsumer<Logger, String> consumer;

    public LogFunction(String name, BiConsumer<Logger, String> consumer) {
        this.name = name;
        this.consumer = consumer;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.requireCount(args, 1);
        FunctionArgs.requireTyped(args, 0, String.class);

        var message = FunctionArgs.getString(args[0]);
        consumer.accept(log, message);
        return BoolStoryValue.TRUE;
    }
}
