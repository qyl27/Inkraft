package cx.rain.mc.inkraft.forge.capability;

import cx.rain.mc.inkraft.mod.InkraftMod;
import cx.rain.mc.inkraft.mod.platform.IStoryHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class InkraftCapabilities {
    public static final ResourceLocation INKRAFT_STORY_STATE_HOLDER_NAME = new ResourceLocation(InkraftMod.MODID, "story_state");

    public static final Capability<IStoryHolder> INKRAFT_STORY_STATE_HOLDER = CapabilityManager.get(new CapabilityToken<>() {});
}
