package cx.rain.mc.inkraft.timer;

import net.minecraft.server.MinecraftServer;

public interface ITaskManager {
    void tick(MinecraftServer server);

    void addTask(InkTask task);

    void removeTask(InkTask task);
}
