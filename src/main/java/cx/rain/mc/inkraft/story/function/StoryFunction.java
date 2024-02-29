package cx.rain.mc.inkraft.story.function;

import cx.rain.mc.inkraft.story.PlayerStory;
import cx.rain.mc.inkraft.utility.StoryVariable;

import java.util.function.Function;

public interface StoryFunction {
    String getName();

    Function<Object[], StoryVariable.IValue> func(PlayerStory story);
}
