package cx.rain.mc.inkraft.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

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
