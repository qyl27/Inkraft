package cx.rain.mc.inkraft.forge;

import cx.rain.mc.inkraft.Inkraft;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Inkraft.MODID)
public class InkraftForge {
    private final Inkraft mod;

    public InkraftForge() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::setup);

        EventBuses.registerModEventBus(Inkraft.MODID, bus);

        mod = new Inkraft();
    }

    private void setup(FMLCommonSetupEvent event) {
        mod.init();
    }
}
