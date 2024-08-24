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

    public void remove(ServerPlayer player) {
        playerStories.remove(player.getUUID());
    }

    public StoryInstance get(ServerPlayer player) {
        var uuid = player.getUUID();
        if (playerStories.containsKey(uuid)) {
            var story = playerStories.get(uuid);
            if (story.getPlayer() != player) {  // Joining game again, new player entity.
                remove(player);
            }
        }

        return playerStories.computeIfAbsent(uuid, __ -> new StoryInstance(logger, this,
                registry, taskManager, player, InkraftPlatform.getPlayerData(player)));
    }

    public boolean isDebug() {
        return debug;
    }
}
