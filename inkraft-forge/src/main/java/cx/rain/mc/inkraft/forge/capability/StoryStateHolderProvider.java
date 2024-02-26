package cx.rain.mc.inkraft.forge.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StoryStateHolderProvider implements ICapabilitySerializable<CompoundTag> {
    private final LazyOptional<StoryStateHolder> optional = LazyOptional.of(StoryStateHolder::new);

    public void invalidate() {
        optional.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
        if (capability == InkraftCapabilities.INKRAFT_STORY_STATE_HOLDER) {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        if (!optional.isPresent()) {
            return new CompoundTag();
        }

        return optional.resolve().get().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (!optional.isPresent()) {
            return;
        }

        optional.resolve().get().deserializeNBT(tag);
    }
}
