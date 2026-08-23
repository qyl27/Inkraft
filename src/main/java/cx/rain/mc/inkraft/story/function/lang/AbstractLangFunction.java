package cx.rain.mc.inkraft.story.function.lang;

import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;

import java.util.function.Supplier;

public abstract class AbstractLangFunction implements IStoryFunction {
    protected final String name;

    protected AbstractLangFunction(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isLookaheadSafe() {
        return true;
    }

    protected static IStoryValue<?, ?> falseOnInvalidInput(
            Supplier<? extends IStoryValue<?, ?>> onFaultyArg) {
        try {
            return onFaultyArg.get();
        } catch (IllegalArgumentException ignored) {
            return BoolStoryValue.FALSE;
        }
    }
}
