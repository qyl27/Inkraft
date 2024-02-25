package cx.rain.mc.inkraft.forge.capability;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.story.state.IInkStoryStateHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class InkraftCapabilities {
    public static final ResourceLocation INKRAFT_STORY_STATE_HOLDER_NAME = new ResourceLocation(Inkraft.MODID, "story_state");

    public static final Capability<IInkStoryStateHolder> INKRAFT_STORY_STATE_HOLDER = CapabilityManager.get(new CapabilityToken<>() {});
}
