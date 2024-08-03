package cx.rain.mc.inkraft.neoforge.platform;

import cx.rain.mc.inkraft.platform.impl.InkPlayerData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class InkPlayerDataNeoForge extends InkPlayerData implements INBTSerializable<CompoundTag> {

    @Override
    public @UnknownNullability CompoundTag serializeNBT(@NotNull HolderLookup.Provider provider) {
        return serialize();
    }

    @Override
    public void deserializeNBT(@NotNull HolderLookup.Provider provider, @NotNull CompoundTag tag) {
        deserialize(tag);
    }
}
