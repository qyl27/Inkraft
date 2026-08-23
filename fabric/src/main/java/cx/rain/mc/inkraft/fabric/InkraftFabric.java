package cx.rain.mc.inkraft.fabric;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.data.story.StoryReloadListener;
import cx.rain.mc.inkraft.fabric.listener.PlayerCopyHandler;
import cx.rain.mc.inkraft.fabric.registry.ModConditions;
import cx.rain.mc.inkraft.fabric.registry.ModStoryFunctions;
import cx.rain.mc.inkraft.listeners.PlayerListeners;
import cx.rain.mc.inkraft.listeners.RegistryListeners;
import cx.rain.mc.inkraft.listeners.ServerListeners;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;

public class InkraftFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Inkraft.init();

        ModConditions.register();
        ModStoryFunctions.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> RegistryListeners.onRegisterCommands(dispatcher));
        ServerTickEvents.END_SERVER_TICK.register(ServerListeners::onServerTick);
        ServerPlayConnectionEvents.JOIN.register((handler, _, _) -> PlayerListeners.onPlayerJoin(handler.player));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, _) -> PlayerListeners.onPlayerQuit(handler.player));
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, _) -> PlayerCopyHandler.copyPlayer(newPlayer.registryAccess(), oldPlayer, newPlayer));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, _) -> {
            PlayerListeners.onPlayerRespawn(newPlayer);
            PlayerCopyHandler.copyPlayer(newPlayer.registryAccess(), oldPlayer, newPlayer);
        });
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(StoryReloadListener.INKRAFT_STORY_LOADER, StoryReloadListener.INSTANCE);
    }
}
