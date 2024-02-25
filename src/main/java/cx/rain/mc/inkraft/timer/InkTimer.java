package cx.rain.mc.inkraft.timer;

import java.util.Objects;

public final class InkTimer {
    private final Runnable runnable;
    private final long initialDelay;
    private final long initialInterval;

    private long delay;
    private long interval;

    public InkTimer(Runnable runnable, long delay, long interval) {
        this.runnable = runnable;
        this.initialDelay = delay;
        this.initialInterval = interval;

        this.delay = delay;
        this.interval = interval;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public long getDelay() {
        return delay;
    }

    public long getInterval() {
        return interval;
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public long getInitialInterval() {
        return initialInterval;
    }

    public void tick() {
        if (delay > 0) {
            delay -= 1;
            return;
        }

        if (interval > 0) {
            interval -= 1;
            return;
        }

        if (interval == 0) {
            runnable.run();
            interval = getInitialInterval();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof InkTimer that) {
            return Objects.equals(this.runnable, that.runnable);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(runnable);
    }

    @Override
    public String toString() {
        return "InkTimer[" +
                "runnable=" + runnable + ", " +
                "delay=" + delay + ", " +
                "interval=" + interval + ']';
    }

}
