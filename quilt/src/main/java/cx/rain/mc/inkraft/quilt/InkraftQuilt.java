package cx.rain.mc.inkraft.quilt;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.command.InkraftCommand;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;
import org.quiltmc.qsl.lifecycle.api.event.ServerTickEvents;

public class InkraftQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        new Inkraft().init();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(InkraftCommand.INKRAFT);
        });

        ServerTickEvents.END.register(server -> {
            Inkraft.getInstance().getTimerManager().onTick(server);
        });
    }
}
