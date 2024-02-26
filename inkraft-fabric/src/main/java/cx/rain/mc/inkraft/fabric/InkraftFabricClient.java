package cx.rain.mc.inkraft.fabric;

import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.gui.VariableHUD;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;

public class InkraftFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(((graphics, delta) -> {
//            if (!InkraftPlatform.getPlayerStoryStateHolder(Minecraft.getInstance().player).getVariables().isEmpty()) {
//                VariableHUD.render(graphics);
//            }
        }));
    }
}
