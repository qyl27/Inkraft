package cx.rain.mc.inkraft.quilt;

import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.gui.VariableHUD;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class InkraftQuiltClient implements ClientModInitializer {
    @Override
    public void onInitializeClient(ModContainer mod) {
        HudRenderCallback.EVENT.register(((graphics, delta) -> {
            if (!InkraftPlatform.getPlayerStoryStateHolder(Minecraft.getInstance().player).getVariables().isEmpty()) {
                VariableHUD.render(graphics);
            }
        }));
    }
}
