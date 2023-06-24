package cx.rain.mc.inkraft.fabric;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.command.InkraftCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class InkraftFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        new Inkraft().init();

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            dispatcher.register(InkraftCommand.INKRAFT);
        }));
    }
}
