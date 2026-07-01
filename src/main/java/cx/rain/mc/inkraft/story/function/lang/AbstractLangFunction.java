package cx.rain.mc.inkraft.story.function.lang;

import cx.rain.mc.inkraft.story.function.IStoryFunction;

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
}
