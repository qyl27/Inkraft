package cx.rain.mc.inkraft.neoforge.data;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.neoforge.data.lang.EnUsProvider;
import cx.rain.mc.inkraft.neoforge.data.lang.ZhCnProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Inkraft.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModData {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var output = gen.getPackOutput();

        gen.addProvider(true, new ZhCnProvider(output));
        gen.addProvider(true, new EnUsProvider(output));
    }
}
