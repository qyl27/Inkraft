package cx.rain.mc.inkraft.quilt.mixins.mixin;

import com.mojang.datafixers.util.Pair;
import cx.rain.mc.inkraft.quilt.mixins.interfaces.IPlayerMixin;
import cx.rain.mc.inkraft.quilt.platform.InkStoryStateHolderQuilt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(Player.class)
public abstract class PlayerMixin implements IPlayerMixin {
    private UUID inkraft$continueToken = UUID.randomUUID();
    private String inkraft$state = "";
    private boolean inkraft$inStory = false;
    private final Map<String, Pair<String, String>> inkraft$variables = new HashMap<>();

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

    @Override
    public Map<String, Pair<String, String>> inkraft$getVariables() {
        return inkraft$variables;
    }

    @Override
    public void inkraft$putVariable(String name, String displayName, boolean isShow, String value) {
        if (isShow) {
            inkraft$variables.put(name, new Pair<>(displayName, value));
        } else {
            inkraft$variables.remove(name);
        }
    }

    @Override
    public void inkraft$clearShowedVariables() {
        inkraft$variables.clear();
    }

    @Inject(at = @At("HEAD"), method = "addAdditionalSaveData")
    private void inkraft$addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        var compound = new CompoundTag();
        compound.putString(InkStoryStateHolderQuilt.TAG_STATE_NAME, inkraft$getState());
        compound.putUUID(InkStoryStateHolderQuilt.TAG_TOKEN_NAME, inkraft$getContinueToken());
        compound.putBoolean(InkStoryStateHolderQuilt.TAG_IN_STORY_NAME, inkraft$isInStory());

        var list = new ListTag();
        for (var entry : inkraft$variables.entrySet()) {
            var compoundTag = new CompoundTag();
            compoundTag.putString(InkStoryStateHolderQuilt.TAG_VARIABLES_NAME_NAME, entry.getKey());
            compoundTag.putString(InkStoryStateHolderQuilt.TAG_VARIABLES_DISPLAY_NAME, entry.getValue().getFirst());
            compoundTag.putString(InkStoryStateHolderQuilt.TAG_VARIABLES_VALUE_NAME, entry.getValue().getSecond());
            list.add(compoundTag);
        }
        tag.put(InkStoryStateHolderQuilt.TAG_VARIABLES_NAME, list);

        tag.put(InkStoryStateHolderQuilt.TAG_INKRAFT_NAME, compound);
    }

    @Inject(at = @At("HEAD"), method = "readAdditionalSaveData")
    private void inkraft$readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        var compound = tag.getCompound(InkStoryStateHolderQuilt.TAG_INKRAFT_NAME);
        inkraft$setState(compound.getString(InkStoryStateHolderQuilt.TAG_STATE_NAME));
        inkraft$setContinueToken(compound.getUUID(InkStoryStateHolderQuilt.TAG_TOKEN_NAME));
        inkraft$setInStory(compound.getBoolean(InkStoryStateHolderQuilt.TAG_IN_STORY_NAME));

        for (var entry : tag.getList(InkStoryStateHolderQuilt.TAG_VARIABLES_NAME, Tag.TAG_COMPOUND)) {
            var compoundTag = (CompoundTag) entry;
            var name = compoundTag.getString(InkStoryStateHolderQuilt.TAG_VARIABLES_NAME_NAME);
            var displayName = compoundTag.getString(InkStoryStateHolderQuilt.TAG_VARIABLES_DISPLAY_NAME);
            var value = compoundTag.getString(InkStoryStateHolderQuilt.TAG_VARIABLES_VALUE_NAME);
            inkraft$putVariable(name, displayName, true, value);
        }
    }
}
