package cx.rain.mc.inkraft.forge.event;

import cx.rain.mc.inkraft.mod.InkraftMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = InkraftMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RenderHUD {
    @SubscribeEvent
    public static void onPreRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
//        var graphics = event.getGuiGraphics();
//
//        if (event.getOverlay() != VanillaGuiOverlay.SCOREBOARD.type()) {
//            return;
//        }
//
//        if (!InkraftPlatform.getPlayerStoryStateHolder(Minecraft.getInstance().player).getVariables().isEmpty()) {
//            VariableHUD.render(graphics);
//        }
    }
}
