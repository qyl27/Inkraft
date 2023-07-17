package cx.rain.mc.inkraft.forge.event;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.InkraftClient;
import cx.rain.mc.inkraft.gui.VariableHUD;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Inkraft.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RenderHUD {
    @SubscribeEvent
    public static void onPreRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        var graphics = event.getGuiGraphics();

        if (!event.getOverlay().id().toString().equals("minecraft:scoreboard")) {
            return;
        }

        if (!InkraftClient.getInstance().getVariables().isEmpty()) {
            VariableHUD.render(graphics);
        }
    }
}
