package cx.rain.mc.inkraft.fabric;

import cx.rain.mc.inkraft.mod.InkraftMod;
import net.fabricmc.api.ModInitializer;

public class InkraftFabric implements ModInitializer {
    private final InkraftMod mod;

    public InkraftFabric() {
        mod = new InkraftMod();
    }

    @Override
    public void onInitialize() {
        mod.init();
    }
}
