package cx.rain.mc.inkraft;

import cx.rain.mc.inkraft.data.InkReloadListener;
import cx.rain.mc.inkraft.data.StoriesManager;
import cx.rain.mc.inkraft.data.loot.predicate.InkraftPredicates;
import cx.rain.mc.inkraft.story.command.StoryCommands;
import cx.rain.mc.inkraft.story.function.StoryFunctions;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Inkraft {
    public static final String MODID = "inkraft";
    public static final String VERSION = "1.20.1-1.0.0";

    private static Inkraft INSTANCE;

    private final InkraftPlatform platform;
    private final StoriesManager storiesManager;

    private Logger logger = LoggerFactory.getLogger(MODID);

    public Inkraft() {
        INSTANCE = this;

        platform = new InkraftPlatform();
        storiesManager = new StoriesManager();

        logger.info("Initializing Inkraft. Ver: " + VERSION);
    }

    public static Inkraft getInstance() {
        return INSTANCE;
    }

    public Logger getLogger() {
        return logger;
    }

    public void init() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new InkReloadListener(), InkReloadListener.INKRAFT_STORY);

        StoryCommands.register();
        StoryFunctions.register();

        InkraftPredicates.register();
    }

    public InkraftPlatform getPlatform() {
        return platform;
    }

    public StoriesManager getStoriesManager() {
        return storiesManager;
    }
}
