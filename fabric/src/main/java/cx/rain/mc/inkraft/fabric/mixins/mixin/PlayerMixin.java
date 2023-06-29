package cx.rain.mc.inkraft.fabric.mixins.mixin;

import cx.rain.mc.inkraft.fabric.mixins.interfaces.IPlayerMixin;
import cx.rain.mc.inkraft.fabric.platform.InkStoryStateHolderFabric;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Player.class)
public abstract class PlayerMixin implements IPlayerMixin {
    private UUID inkraft$continueToken = UUID.randomUUID();
    private String inkraft$state = "";
    private boolean inkraft$inStory = false;

    @Override
    public String inkraft$getState() {
        return inkraft$state;
    }

    @Override
    public void inkraft$setState(String state) {
        inkraft$state = state;
    }

    @Override
    public UUID inkraft$getContinueToken() {
        return inkraft$continueToken;
    }

    @Override
    public void inkraft$setContinueToken(UUID token) {
        inkraft$continueToken = token;
    }

    @Override
    public boolean inkraft$isInStory() {
        return inkraft$inStory;
    }

    @Override
    public void inkraft$setInStory(boolean inStory) {
        inkraft$inStory = inStory;
    }

    @Inject(at = @At("HEAD"), method = "addAdditionalSaveData")
    private void inkraft$addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        var compound = new CompoundTag();
        compound.putString(InkStoryStateHolderFabric.TAG_STATE_NAME, inkraft$getState());
        compound.putUUID(InkStoryStateHolderFabric.TAG_TOKEN_NAME, inkraft$getContinueToken());
        compound.putBoolean(InkStoryStateHolderFabric.TAG_IN_STORY_NAME, inkraft$isInStory());
        tag.put(InkStoryStateHolderFabric.TAG_INKRAFT_NAME, compound);
    }

    @Inject(at = @At("HEAD"), method = "readAdditionalSaveData")
    private void inkraft$readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        var compound = tag.getCompound(InkStoryStateHolderFabric.TAG_INKRAFT_NAME);
        inkraft$setState(compound.getString(InkStoryStateHolderFabric.TAG_STATE_NAME));
        inkraft$setContinueToken(compound.getUUID(InkStoryStateHolderFabric.TAG_TOKEN_NAME));
        inkraft$setInStory(compound.getBoolean(InkStoryStateHolderFabric.TAG_IN_STORY_NAME));
    }
}
