package cx.rain.mc.inkraft.fabric.mixins.interfaces;

import com.mojang.datafixers.util.Pair;

import java.util.Map;
import java.util.UUID;

public interface IPlayerMixin {
    String inkraft$getState();
    void inkraft$setState(String state);

    UUID inkraft$getContinueToken();
    void inkraft$setContinueToken(UUID token);

    boolean inkraft$isInStory();
    void inkraft$setInStory(boolean inStory);

    Map<String, Pair<String, String>> inkraft$getVariables();
    void inkraft$putVariable(String name, String displayName, boolean isShow, String value);
    void inkraft$clearShowedVariables();
}
