package cx.rain.mc.inkraft.timer;

import cx.rain.mc.inkraft.timer.cancellation.CancellableToken;
import cx.rain.mc.inkraft.timer.cancellation.ICancellationToken;
import net.minecraft.server.MinecraftServer;

public interface ITaskManager {
    void tick(MinecraftServer server);

    void addTask(InkTask task);

    void removeTask(InkTask task);

    default void runInstant(Runnable runnable) {
        addTask(new InkTask(runnable, () -> false, 0, -1));
    }

    default void runOneShot(Runnable runnable, long delay) {
        addTask(new InkTask(runnable, () -> false, delay, -1));
    }

    default CancellableToken runPeriodic(Runnable runnable, long interval) {
        var cancellable = new CancellableToken();
        addTask(new InkTask(runnable, cancellable, 0, interval));
        return cancellable;
    }

    default void run(Runnable runnable, ICancellationToken cancellationToken, long delay, long interval) {
        addTask(new InkTask(runnable, cancellationToken, delay, interval));
    }
}
