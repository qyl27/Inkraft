package cx.rain.mc.inkraft.fabric;

import cx.rain.mc.inkraft.Inkraft;
import net.fabricmc.api.ModInitializer;

public class InkraftFabric implements ModInitializer {
    private final Inkraft mod;

    public InkraftFabric() {
        mod = new Inkraft();
    }

    @Override
    public void onInitialize() {
        mod.init();
    }
}
