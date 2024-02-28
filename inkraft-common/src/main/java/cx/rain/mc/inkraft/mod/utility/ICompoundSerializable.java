package cx.rain.mc.inkraft.mod.utility;

import net.minecraft.nbt.CompoundTag;

public interface ICompoundSerializable {
    CompoundTag serialize();

    void deserialize(CompoundTag tag);
}
