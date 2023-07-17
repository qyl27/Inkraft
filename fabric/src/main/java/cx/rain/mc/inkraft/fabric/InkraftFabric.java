package cx.rain.mc.inkraft.fabric;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.command.InkraftCommand;
import cx.rain.mc.inkraft.gui.VariableHUD;
import dev.architectury.event.events.common.TickEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class InkraftFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        new Inkraft().init();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(InkraftCommand.INKRAFT);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Inkraft.getInstance().getTimerManager().onTick(server);
        });

        HudRenderCallback.EVENT.register(((graphics, delta) -> {
            VariableHUD.render(graphics);
        }));
    }
}
