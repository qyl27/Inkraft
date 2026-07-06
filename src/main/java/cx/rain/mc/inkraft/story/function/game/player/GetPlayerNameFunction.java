package cx.rain.mc.inkraft.story.function.game.player;

import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.value.StringStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class GetPlayerNameFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "getPlayerName";
    }

    @Override
    public StringStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.requireCount(args, 0);

        return new StringStoryValue(instance.getPlayer().getName().getString());
    }
}
