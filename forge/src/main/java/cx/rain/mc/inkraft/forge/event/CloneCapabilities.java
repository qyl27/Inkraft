package cx.rain.mc.inkraft.forge.event;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.forge.capability.InkraftCapabilities;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Inkraft.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CloneCapabilities {
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(InkraftCapabilities.INKRAFT_STORY_STATE_HOLDER).ifPresent(cap -> {
                event.getEntity().getCapability(InkraftCapabilities.INKRAFT_STORY_STATE_HOLDER).ifPresent(newCap -> {
                    newCap.setState(cap.getState());
                    newCap.setContinueToken(cap.getContinueToken());

                    newCap.clearVariables();
                    for (var entry : cap.getVariables().entrySet()) {
                        var value = entry.getValue();
                        newCap.putVariable(entry.getKey(), value.getLeft(), value.getMiddle(), value.getRight());
                    }
                });
            });
        }
    }
}
