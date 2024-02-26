package cx.rain.mc.inkraft.timer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.function.Consumer;

public class TimerManager implements ITimerManager {
    private final Map<UUID, List<InkTimer>> runnableMap = new HashMap<>();
    private final List<Consumer<TimerManager>> doAfter = new ArrayList<>();

    public void onTick(MinecraftServer server) {
        for (Map.Entry<UUID, List<InkTimer>> entry : runnableMap.entrySet()) {
            List<InkTimer> timers = entry.getValue();
            for (InkTimer timer : timers) {
                timer.tick();
            }
        }

        for (var func : doAfter) {
            func.accept(this);
        }
        doAfter.clear();
    }

    @Override
    public void addTimer(ServerPlayer player, Runnable runnable, long delay, long interval) {
        var uuid = player.getUUID();
        if (!runnableMap.containsKey(uuid)) {
            runnableMap.put(uuid, new ArrayList<>());
        }

        runnableMap.get(uuid).add(new InkTimer(runnable, delay, interval));
    }

    @Override
    public void removeTimers(ServerPlayer player) {
        doAfter.add(manager -> {
            runnableMap.remove(player.getUUID());
        });
    }

    @Override
    public void clearTimers() {
        doAfter.add(manager -> {
            runnableMap.clear();
        });
    }
}
