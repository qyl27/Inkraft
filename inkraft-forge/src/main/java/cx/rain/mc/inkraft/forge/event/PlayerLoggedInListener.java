package cx.rain.mc.inkraft.forge.event;

import cx.rain.mc.inkraft.Constants;
import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.InkraftPlatform;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Inkraft.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerLoggedInListener {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        var player = event.getEntity();
        var holder = InkraftPlatform.getPlayerStoryStateHolder(player);

        if (holder.isInStory()) {
            var component = Component.translatable(Constants.MESSAGE_STORY_LOGGED_IN_CONTINUE);
            component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inkraft repeat")));
            player.sendSystemMessage(component);
        }
    }
}
