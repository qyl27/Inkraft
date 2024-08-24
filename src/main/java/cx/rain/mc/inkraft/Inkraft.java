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
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
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
            properties.load(Inkraft.class.getResourceAsStream("/build_info.properties"));
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

        logger.info("Initializing Inkraft. Ver: {}, Build at: {}", VERSION, BUILD_TIME != null ? BUILD_TIME : "B.C. 3200");

        storyRegistry = new StoryRegistry();
        timerManager = new TaskManager();
        storiesManager = new StoriesManager(logger, storyRegistry, timerManager, true);

        InkraftNetworking.register();
        StoryFunctions.register();
        ModConditions.register();
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

        PlayerEvent.PLAYER_JOIN.register(player -> {
            var story = storiesManager.get(player);

            if (!story.isStoryEnded()) {
                var component = Component.translatable(ModConstants.Messages.STORY_RESUME).withStyle(ChatFormatting.GREEN);
                component.setStyle(component.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inkraft current")));
                component.setStyle(component.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(ModConstants.Messages.STORY_RESUME_HINT).withStyle(ChatFormatting.YELLOW))));
                player.sendSystemMessage(component);
            }
        });
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
