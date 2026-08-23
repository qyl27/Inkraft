package cx.rain.mc.inkraft.listeners;

import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.engine.EngineManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;

public class PlayerListeners {
    public static void onPlayerJoin(ServerPlayer player) {
        onPlayerAvailable(player);
    }

    public static void onPlayerRespawn(ServerPlayer player) {
        onPlayerAvailable(player);
    }

    private static void onPlayerAvailable(ServerPlayer player) {
        var story = EngineManager.getInstance().get(player);

        if (!story.isStoryEnded()) {
            var component = Component.translatable(ModConstants.Messages.STORY_RESUME).withStyle(ChatFormatting.YELLOW);
            component.setStyle(component.getStyle().withClickEvent(new ClickEvent.RunCommand("/inkraft current")));
            component.setStyle(component.getStyle().withHoverEvent(new HoverEvent.ShowText(Component.translatable(ModConstants.Messages.STORY_RESUME_HINT).withStyle(ChatFormatting.YELLOW))));
            player.sendSystemMessage(component);
        }
    }

    public static void onPlayerQuit(ServerPlayer player) {
        EngineManager.getInstance().remove(player);
    }
}
