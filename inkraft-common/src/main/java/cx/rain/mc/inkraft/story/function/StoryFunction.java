package cx.rain.mc.inkraft.story.function;

import cx.rain.mc.inkraft.story.PlayerStory;
import cx.rain.mc.inkraft.story.StoryVariables;

import java.util.function.Function;

public interface StoryFunction {
    String getName();

    Function<Object[], StoryVariables.IValue> func(PlayerStory engine);
}
