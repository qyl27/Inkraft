package cx.rain.mc.inkraft.neoforge;

import cx.rain.mc.inkraft.Inkraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(Inkraft.MODID)
public class InkraftNeoForge {
    private final Inkraft mod;

    public InkraftNeoForge(IEventBus bus) {
        bus.addListener(this::setup);
        mod = new Inkraft();
    }

    private void setup(FMLCommonSetupEvent event) {
        mod.init();
    }
}
