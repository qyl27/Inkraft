package cx.rain.mc.inkraft.timer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public interface ITimerManager {
    default void addTimer(ServerPlayer player, Runnable runnable) {
        addTimer(player, runnable, 0, -1);
    }

    void onTick(MinecraftServer server);

    void addTimer(ServerPlayer player, Runnable runnable, long delay, long interval);

    void removeTimers(ServerPlayer player);

    void clearTimers();
}
