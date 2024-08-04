package cx.rain.mc.inkraft;

import cx.rain.mc.inkraft.command.InkraftCommand;
import cx.rain.mc.inkraft.data.story.StoryReloadListener;
import cx.rain.mc.inkraft.data.story.StoryRegistry;
import cx.rain.mc.inkraft.data.loot.condition.ModConditions;
import cx.rain.mc.inkraft.networking.InkraftNetworking;
import cx.rain.mc.inkraft.story.StoriesManager;
import cx.rain.mc.inkraft.story.function.StoryFunctions;
import cx.rain.mc.inkraft.timer.ITaskManager;
import cx.rain.mc.inkraft.timer.TaskManager;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.commands.Commands;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Properties;

public class Inkraft {
    public static final String MODID = "inkraft";
    public static final String VERSION;
    public static final ZonedDateTime BUILD_TIME;

    static {
        var properties = new Properties();
        String version;
        ZonedDateTime buildTime;
        try {
            properties.load(Inkraft.class.getResourceAsStream("/build.properties"));
            version = properties.getProperty("mod_version") + "+mc" + properties.getProperty("minecraft_version");
            buildTime = ZonedDateTime.parse(properties.getProperty("build_time"));
        } catch (Exception ignored) {
            version = "Unknown";
            buildTime = null;
        }
        BUILD_TIME = buildTime;
        VERSION = version;
    }

    private static Inkraft INSTANCE;

    private final StoryRegistry storyRegistry;
    private final ITaskManager timerManager;
    private final StoriesManager storiesManager;

    private final Logger logger = LoggerFactory.getLogger(MODID);

    public Inkraft() {
        INSTANCE = this;

        storyRegistry = new StoryRegistry();
        timerManager = new TaskManager();
        storiesManager = new StoriesManager(logger, storyRegistry, timerManager, true);

        logger.info("Initializing Inkraft. Ver: {}, Build at: {}", VERSION, BUILD_TIME != null ? BUILD_TIME : "B.C. 3200");
    }

    public static Inkraft getInstance() {
        return INSTANCE;
    }

    public void init() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) ->
                dispatcher.register(InkraftCommand.INKRAFT));

        TickEvent.SERVER_POST.register(timerManager::tick);

        ReloadListenerRegistry.register(PackType.SERVER_DATA, new StoryReloadListener(storyRegistry),
                StoryReloadListener.INKRAFT_STORY_LOADER);

//        PlayerEvent.PLAYER_JOIN.register(player -> {
//            var holder = InkraftPlatform.getPlayerStoryStateHolder(player);
//
//            if (holder.isInStory()) {
//                var component = Component.translatable(Constants.MESSAGE_STORY_LOGGED_IN_CONTINUE);
//                component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
//                        "/inkraft repeat")));
//                player.sendSystemMessage(component);
//            }
//        });

        InkraftNetworking.register();
        StoryFunctions.register();
        ModConditions.register();
    }

    public Logger getLogger() {
        return logger;
    }

    public StoryRegistry getStoryRegistry() {
        return storyRegistry;
    }

    public ITaskManager getTimerManager() {
        return timerManager;
    }

    public StoriesManager getStoriesManager() {
        return storiesManager;
    }
}
