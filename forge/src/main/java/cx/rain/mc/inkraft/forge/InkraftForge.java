package cx.rain.mc.inkraft.forge;

import cx.rain.mc.inkraft.Inkraft;
import net.minecraftforge.fml.common.Mod;

@Mod(Inkraft.MODID)
public class InkraftForge {
    public InkraftForge() {
        new Inkraft().init();
    }
}
