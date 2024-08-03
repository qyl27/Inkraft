package cx.rain.mc.inkraft;

import cx.rain.mc.inkraft.command.InkraftCommand;
import cx.rain.mc.inkraft.data.story.StoryReloadListener;
import cx.rain.mc.inkraft.data.story.StoriesManager;
import cx.rain.mc.inkraft.data.loot.predicate.InkraftPredicates;
import cx.rain.mc.inkraft.networking.InkraftNetworking;
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
import java.util.Properties;

public class Inkraft {
    public static final String MODID = "inkraft";
    public static final String VERSION;
    public static final ZonedDateTime BUILD_TIME;

    static {
        var properties = new Properties();
        String version = "Unknown";
        ZonedDateTime buildTime = null;
        try {
            properties.load(Inkraft.class.getResourceAsStream("/build.properties"));
            version = properties.getProperty("mod_version") + "+mc" + properties.getProperty("minecraft_version");
            buildTime = ZonedDateTime.parse(properties.getProperty("build_time"));
        } catch (IOException ignored) {
        }
        BUILD_TIME = buildTime;
        VERSION = version;
    }

    private static Inkraft INSTANCE;

    private final StoriesManager storiesManager;
    private final ITaskManager timerManager;

    private final Logger logger = LoggerFactory.getLogger(MODID);

    public Inkraft() {
        INSTANCE = this;

        storiesManager = new StoriesManager();
        timerManager = new TaskManager();

        logger.info("Initializing Inkraft. Ver: {}, Build at: {}", VERSION, BUILD_TIME != null ? BUILD_TIME : "BC 3200");
    }

    public static Inkraft getInstance() {
        return INSTANCE;
    }

    public Logger getLogger() {
        return logger;
    }

    public void init() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> {
            if (selection == Commands.CommandSelection.ALL) {
                dispatcher.register(InkraftCommand.INKRAFT);
            }
        });

        TickEvent.SERVER_POST.register(server -> getTimerManager().tick(server));

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

        ReloadListenerRegistry.register(PackType.SERVER_DATA, new StoryReloadListener(),
                StoryReloadListener.INKRAFT_STORY_LOADER);

        InkraftNetworking.register();
        StoryFunctions.register();
        InkraftPredicates.register();
    }

    public StoriesManager getStoriesManager() {
        return storiesManager;
    }

    public ITaskManager getTimerManager() {
        return timerManager;
    }
}
