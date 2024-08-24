package cx.rain.mc.inkraft.story.function.game.player;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;

public class GetPlayerNameFunction implements IStoryFunction {
    @Override
    public String getName() {
        return "getPlayerName";
    }

    @Override
    public IStoryVariable.Str apply(StoryInstance instance, Object... args) {
        return new IStoryVariable.Str(instance.getPlayer().getName().getString());
    }
}
