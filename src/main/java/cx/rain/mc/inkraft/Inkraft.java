package cx.rain.mc.inkraft;

import cx.rain.mc.inkraft.command.InkraftCommand;
import cx.rain.mc.inkraft.data.InkReloadListener;
import cx.rain.mc.inkraft.data.StoriesManager;
import cx.rain.mc.inkraft.data.loot.predicate.InkraftPredicates;
import cx.rain.mc.inkraft.networking.InkraftNetworking;
import cx.rain.mc.inkraft.story.function.StoryFunctions;
import cx.rain.mc.inkraft.timer.ITimerManager;
import cx.rain.mc.inkraft.timer.TimerManager;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class Inkraft {
    public static final String MODID = "inkraft";
    public static final String VERSION;

    static {
        var properties = new Properties();
        var version = "";
        try {
            properties.load(Inkraft.class.getResourceAsStream("/build.properties"));
            version = properties.getProperty("mod_version") + "+mc" + properties.getProperty("minecraft_version");
        } catch (IOException ex) {
            version = "Unknown";
        }
        VERSION = version;
    }

    private static Inkraft INSTANCE;

    private final StoriesManager storiesManager;
    private final ITimerManager timerManager;
    private final InkraftNetworking networking;

    private final Logger logger = LoggerFactory.getLogger(MODID);

    public Inkraft() {
        INSTANCE = this;

        storiesManager = new StoriesManager();
        timerManager = new TimerManager();
        networking = new InkraftNetworking();

        logger.info("Initializing Inkraft. Ver: " + VERSION);
    }

    public static Inkraft getInstance() {
        return INSTANCE;
    }

    public Logger getLogger() {
        return logger;
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

    public InkraftNetworking getNetworking() {
        return networking;
    }
}
