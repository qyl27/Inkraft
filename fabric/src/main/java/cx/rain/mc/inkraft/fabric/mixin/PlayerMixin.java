package cx.rain.mc.inkraft.fabric.mixin;

import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.fabric.interfaces.IPlayerMixin;
import cx.rain.mc.inkraft.platform.impl.InkPlayerData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin implements IPlayerMixin {
    @Unique
    private final InkPlayerData inkraft$data = new InkPlayerData();

    @Override
    public InkPlayerData inkraft$getData() {
        return inkraft$data;
    }

    @Inject(at = @At("HEAD"), method = "addAdditionalSaveData")
    private void afterSave(CompoundTag tag, CallbackInfo ci) {
        var compound = inkraft$data.serialize();
        tag.put(ModConstants.Tags.FABRIC_TAG_NAME, compound);
    }


    @Inject(at = @At("HEAD"), method = "readAdditionalSaveData")
    private void afterRead(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains(ModConstants.Tags.FABRIC_TAG_NAME)) {
            var compound = tag.getCompound(ModConstants.Tags.FABRIC_TAG_NAME);
            inkraft$data.deserialize(compound);
        }
    }
}
