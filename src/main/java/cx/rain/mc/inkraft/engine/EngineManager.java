package cx.rain.mc.inkraft.engine;

import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.data.story.IDataRegistry;
import cx.rain.mc.inkraft.data.story.StoryRegistry;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.timer.ITaskManager;
import cx.rain.mc.inkraft.timer.TaskManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class EngineManager {
    private static final EngineManager INSTANCE = new EngineManager();

    public static EngineManager getInstance() {
        return INSTANCE;
    }

    @Getter
    private final IDataRegistry<Identifier, String> storyRegistry = new StoryRegistry();

    @Getter
    private final ITaskManager taskManager = new TaskManager();

    @Getter
    private final boolean debug = "true".equalsIgnoreCase(System.getProperty("inkraft.debug"));

    private final Map<UUID, StoryInstance> playerStories = new HashMap<>();

    public void remove(ServerPlayer player) {
        var story = playerStories.remove(player.getUUID());
        if (story != null) {
            story.stop();
        }
    }

    public StoryInstance get(ServerPlayer player) {
        var uuid = player.getUUID();
        if (playerStories.containsKey(uuid)) {
            var story = playerStories.get(uuid);
            if (story.getPlayer() != player) {  // Joining game again, new player entity.
                remove(player);
            }
        }

        return playerStories.computeIfAbsent(uuid, _ ->
                new StoryInstance(this, player, InkraftPlatform.getPlayerData(player)));
    }
}
