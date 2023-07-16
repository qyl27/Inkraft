package cx.rain.mc.inkraft.quilt.platform;

import cx.rain.mc.inkraft.quilt.mixins.interfaces.IPlayerMixin;
import cx.rain.mc.inkraft.story.state.IInkStoryStateHolder;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class InkStoryStateHolderQuilt implements IInkStoryStateHolder {
    public static final String TAG_INKRAFT_NAME = "inkraft";
    public static final String TAG_STATE_NAME = "state";
    public static final String TAG_TOKEN_NAME = "token";
    public static final String TAG_IN_STORY_NAME = "inStory";

    private Player player;

    public InkStoryStateHolderQuilt(Player player) {
        this.player = player;
    }

    @Override
    public String getState() {
        return ((IPlayerMixin) player).inkraft$getState();
    }

    @Override
    public void setState(String state) {
        ((IPlayerMixin) player).inkraft$setState(state);
    }

    @Override
    public UUID getContinueToken() {
        return ((IPlayerMixin) player).inkraft$getContinueToken();
    }

    @Override
    public void setContinueToken(UUID token) {
        ((IPlayerMixin) player).inkraft$setContinueToken(token);
    }

    @Override
    public boolean isInStory() {
        return ((IPlayerMixin) player).inkraft$isInStory();
    }

    @Override
    public void setInStory(boolean inStory) {
        ((IPlayerMixin) player).inkraft$setInStory(inStory);
    }

    @Override
    public void clearState() {
        setState("");
        setInStory(false);
    }
}
