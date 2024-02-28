package cx.rain.mc.inkraft.forge;

import cx.rain.mc.inkraft.mod.InkraftMod;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(InkraftMod.MODID)
public class InkraftForge {
    private final InkraftMod mod;

    public InkraftForge() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::setup);

        EventBuses.registerModEventBus(InkraftMod.MODID, bus);

        mod = new InkraftMod();
    }

    private void setup(FMLCommonSetupEvent event) {
        mod.init();
    }
}
