package cx.rain.mc.inkraft.mod;

import cx.rain.mc.inkraft.Constants;
import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.mod.command.InkraftCommand;
import cx.rain.mc.inkraft.mod.data.InkReloadListener;
import cx.rain.mc.inkraft.story.StoriesManager;
import cx.rain.mc.inkraft.mod.data.loot.predicate.InkraftPredicates;
import cx.rain.mc.inkraft.mod.networking.ModNetworking;
import cx.rain.mc.inkraft.story.function.StoryFunctions;
import cx.rain.mc.inkraft.mod.timer.ITimerManager;
import cx.rain.mc.inkraft.mod.timer.TimerManager;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InkraftMod {
    private static InkraftMod INSTANCE;

    private final StoriesManager storiesManager = new StoriesManager();
    private final ITimerManager timerManager = new TimerManager();
    private final ModNetworking networking = new ModNetworking();

    private final Logger logger = LoggerFactory.getLogger(Inkraft.ID);

    public InkraftMod() {
        INSTANCE = this;

        logger.info("Initializing Inkraft. Ver: " + Inkraft.VERSION);
    }

    public static InkraftMod getInstance() {
        return INSTANCE;
    }

    public void init() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) ->
                dispatcher.register(InkraftCommand.INKRAFT));

        TickEvent.SERVER_POST.register(server -> getTimerManager().onTick(server));

        PlayerEvent.PLAYER_JOIN.register(player -> {
            var holder = InkraftPlatform.getPlayerStoryStateHolder(player);

            if (holder.isInStory()) {
                var component = Component.translatable(Constants.MESSAGE_STORY_LOGGED_IN_CONTINUE);
                component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/inkraft repeat")));
                player.sendSystemMessage(component);
            }
        });

        ReloadListenerRegistry.register(PackType.SERVER_DATA, new InkReloadListener(),
                InkReloadListener.INKRAFT_STORY);

        StoryFunctions.register();

        InkraftPredicates.register();
    }

    public StoriesManager getStoriesManager() {
        return storiesManager;
    }

    public ITimerManager getTimerManager() {
        return timerManager;
    }

    public ModNetworking getNetworking() {
        return networking;
    }

    public Logger getLogger() {
        return logger;
    }
}
