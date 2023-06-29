package cx.rain.mc.inkraft.fabric.mixins.interfaces;

import java.util.UUID;

public interface IPlayerMixin {
    String inkraft$getState();
    void inkraft$setState(String state);

    UUID inkraft$getContinueToken();
    void inkraft$setContinueToken(UUID token);

    boolean inkraft$isInStory();
    void inkraft$setInStory(boolean inStory);
}
