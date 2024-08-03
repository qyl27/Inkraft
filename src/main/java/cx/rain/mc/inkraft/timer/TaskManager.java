package cx.rain.mc.inkraft.timer;

import net.minecraft.server.MinecraftServer;

import java.util.*;

public class TaskManager implements ITaskManager {
    private final List<InkTask> tasks = new ArrayList<>();

    private final List<InkTask> toAdd = new ArrayList<>();
    private final List<InkTask> toRemove = new ArrayList<>();

    public void tick(MinecraftServer server) {
        tasks.addAll(toAdd);
        toAdd.clear();
        tasks.removeAll(toRemove);
        toRemove.clear();

        var it = tasks.iterator();
        while (it.hasNext()) {
            var task = it.next();
            server.execute(task::tick);

            if (task.isStopped()) {
                it.remove();
            }
        }
    }

    @Override
    public void addTask(InkTask task) {
        toAdd.add(task);
    }

    @Override
    public void removeTask(InkTask task) {
        toRemove.add(task);
    }
}
