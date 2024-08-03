package cx.rain.mc.inkraft.timer;

import cx.rain.mc.inkraft.timer.cancellation.CancellableToken;
import net.minecraft.server.MinecraftServer;

public interface ITaskManager {
    void tick(MinecraftServer server);

    void addTask(InkTask task);

    void removeTask(InkTask task);

    default void runInstant(Runnable runnable) {
        addTask(new InkTask(runnable, () -> true, 0, -1));
    }

    default void runOneShot(Runnable runnable, long delay) {
        addTask(new InkTask(runnable, () -> true, delay, -1));
    }

    default CancellableToken runPeriodic(Runnable runnable, long interval) {
        var cancellable = new CancellableToken();
        addTask(new InkTask(runnable, cancellable, 0, interval));
        return cancellable;
    }
}
