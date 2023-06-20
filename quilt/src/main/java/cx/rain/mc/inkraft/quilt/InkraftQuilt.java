package cx.rain.mc.inkraft.quilt;

import cx.rain.mc.inkraft.Inkraft;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class InkraftQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        new Inkraft().init();
    }
}
