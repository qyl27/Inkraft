package cx.rain.mc.inkraft.story;

import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.data.story.StoryRegistry;
import cx.rain.mc.inkraft.timer.ITaskManager;
import net.minecraft.server.level.ServerPlayer;

public class StoriesManager {
    private final StoryRegistry registry;
    private final ITaskManager taskManager;

    private final boolean debug;

    public StoriesManager(StoryRegistry registry, ITaskManager taskManager, boolean debug) {
        this.registry = registry;
        this.taskManager = taskManager;
        this.debug = debug;
    }

    public StoryInstance createForPlayer(ServerPlayer player) {
        return new StoryInstance(this, registry, taskManager, player, InkraftPlatform.getPlayerStoryStateHolder(player));
    }

    public boolean isDebug() {
        return debug;
    }
}
