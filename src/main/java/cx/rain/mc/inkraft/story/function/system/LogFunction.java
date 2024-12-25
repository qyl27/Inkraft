package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import org.slf4j.Logger;

import java.util.function.BiConsumer;

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
    public IStoryVariable.Bool apply(StoryInstance instance, String... args) {
        var message = args[0];
        consumer.accept(Inkraft.getInstance().getLogger(), message);
        return IStoryVariable.Bool.TRUE;
    }
}
