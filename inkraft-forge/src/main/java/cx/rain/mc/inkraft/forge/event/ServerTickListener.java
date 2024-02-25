package cx.rain.mc.inkraft.forge.event;

import cx.rain.mc.inkraft.Inkraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Inkraft.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerTickListener {
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        Inkraft.getInstance().getTimerManager().onTick(event.getServer());
    }
}
