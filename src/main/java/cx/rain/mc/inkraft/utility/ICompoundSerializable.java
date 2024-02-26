package cx.rain.mc.inkraft.utility;

import net.minecraft.nbt.CompoundTag;

public interface ICompoundSerializable {
    CompoundTag serialize();

    void deserialize(CompoundTag tag);
}
