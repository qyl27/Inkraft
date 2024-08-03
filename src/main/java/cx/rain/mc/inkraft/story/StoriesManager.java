package cx.rain.mc.inkraft.story;

import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.data.story.StoryRegistry;
import cx.rain.mc.inkraft.timer.ITaskManager;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StoriesManager {
    private final Logger logger;
    private final StoryRegistry registry;
    private final ITaskManager taskManager;

    private final boolean debug;

    private final Map<UUID, StoryInstance> playerStories = new HashMap<>();

    public StoriesManager(Logger logger, StoryRegistry registry, ITaskManager taskManager, boolean debug) {
        this.logger = logger;
        this.registry = registry;
        this.taskManager = taskManager;
        this.debug = debug;
    }

    public StoryInstance get(ServerPlayer player) {
        return playerStories.computeIfAbsent(player.getUUID(), uuid -> new StoryInstance(logger, this,
                registry, taskManager, player, InkraftPlatform.getPlayerData(player)));
    }

    public boolean isDebug() {
        return debug;
    }
}
