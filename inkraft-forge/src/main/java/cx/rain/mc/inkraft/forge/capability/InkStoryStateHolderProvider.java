package cx.rain.mc.inkraft.forge.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InkStoryStateHolderProvider implements ICapabilitySerializable<CompoundTag> {
    private final InkStoryStateHolder inkStoryStateHolder = new InkStoryStateHolder();
    private final LazyOptional<InkStoryStateHolder> holderOptional = LazyOptional.of(() -> inkStoryStateHolder);

    public void invalidate() {
        holderOptional.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
        if (capability == InkraftCapabilities.INKRAFT_STORY_STATE_HOLDER) {
            return holderOptional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        if (!holderOptional.isPresent()) {
            return new CompoundTag();
        }

        return holderOptional.resolve().get().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (!holderOptional.isPresent()) {
            return;
        }

        holderOptional.resolve().get().deserializeNBT(tag);
    }
}
