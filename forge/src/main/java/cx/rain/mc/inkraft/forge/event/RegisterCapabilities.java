package cx.rain.mc.inkraft.forge.event;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.forge.capability.InkStoryStateHolderProvider;
import cx.rain.mc.inkraft.forge.capability.InkraftCapabilities;
import cx.rain.mc.inkraft.story.IInkStoryStateHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Inkraft.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RegisterCapabilities {
    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IInkStoryStateHolder.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            var provider = new InkStoryStateHolderProvider();
            event.addCapability(InkraftCapabilities.INKRAFT_STORY_STATE_HOLDER_NAME, provider);
            event.addListener(provider::invalidate);
        }
    }
}
