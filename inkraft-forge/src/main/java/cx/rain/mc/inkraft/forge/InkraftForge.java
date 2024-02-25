package cx.rain.mc.inkraft.forge;

import cx.rain.mc.inkraft.Inkraft;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Inkraft.MODID)
public class InkraftForge {
    public InkraftForge() {
        EventBuses.registerModEventBus(Inkraft.MODID, FMLJavaModLoadingContext.get().getModEventBus());

        new Inkraft().init();
    }
}
