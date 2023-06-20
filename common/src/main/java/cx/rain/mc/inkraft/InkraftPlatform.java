package cx.rain.mc.inkraft;

import cx.rain.mc.inkraft.story.IInkStoryStateHolder;
import dev.architectury.injectables.annotations.ExpectPlatform;

public class InkraftPlatform {
    @ExpectPlatform
    public IInkStoryStateHolder getPlayerStoryStateHolder() {
        throw new RuntimeException();
    }
}
