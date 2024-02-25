package cx.rain.mc.inkraft.fabric;

import cx.rain.mc.inkraft.Constants;
import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.command.InkraftCommand;
import cx.rain.mc.inkraft.gui.VariableHUD;
import dev.architectury.event.events.common.TickEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

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

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var player = handler.player;
            var holder = InkraftPlatform.getPlayerStoryStateHolder(player);

            if (holder.isInStory()) {
                var component = Component.translatable(Constants.MESSAGE_STORY_LOGGED_IN_CONTINUE);
                component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inkraft repeat")));
                player.sendSystemMessage(component);
            }
        });
    }
}
