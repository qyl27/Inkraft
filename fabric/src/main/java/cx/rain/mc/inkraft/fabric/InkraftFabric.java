package cx.rain.mc.inkraft.fabric;

import cx.rain.mc.inkraft.Inkraft;
import net.fabricmc.api.ModInitializer;

public class InkraftFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        new Inkraft().init();
    }
}
