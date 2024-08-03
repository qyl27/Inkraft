package cx.rain.mc.inkraft.timer;

import cx.rain.mc.inkraft.timer.cancellation.ICancellationToken;

import java.util.Objects;

public final class InkTask {
    private final Runnable runnable;
    private final ICancellationToken cancellationToken;

    private final long initialDelay;
    private final long initialInterval;

    private long delayRemain;
    private long intervalRemain;

    /**
     * A task runs with a delay and an interval.
     * @param runnable Task runnable.
     * @param cancellationToken Task cancellation token.
     * @param delay Task run delay, 0 for run instant, positive for after N ticks to run.
     * @param interval Task run interval, -1 for one-shot, positive for run again after N ticks.
     */
    public InkTask(Runnable runnable, ICancellationToken cancellationToken, long delay, long interval) {
        this.runnable = runnable;
        this.cancellationToken = cancellationToken;
        this.initialDelay = delay;
        this.initialInterval = interval;

        this.delayRemain = delay;
        this.intervalRemain = interval == -1 ? 0 : interval;
    }

    public void tick() {
        if (isStopped()) {
            return;
        }

        if (delayRemain > 0) {
            delayRemain -= 1;
            return;
        }

        if (intervalRemain > 0) {
            intervalRemain -= 1;
            return;
        }

        if (delayRemain == 0 && intervalRemain == 0) {
            runnable.run();
            intervalRemain = initialInterval;
        }
    }

    public boolean isStopped() {
        if (initialInterval == -1) {
            return delayRemain == 0 || cancellationToken.isCancelled();
        } else {
            return delayRemain == 0 && cancellationToken.isCancelled();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof InkTask that) {
            return Objects.equals(this.runnable, that.runnable)
                    && cancellationToken.equals(that.cancellationToken)
                    && initialDelay == that.initialDelay
                    && initialInterval == that.initialInterval
                    && delayRemain == that.delayRemain
                    && intervalRemain == that.intervalRemain;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(runnable) + Objects.hash(cancellationToken)
                + Objects.hash(initialDelay) + Objects.hash(initialInterval)
                + Objects.hash(delayRemain) + Objects.hash(intervalRemain);
    }

    @Override
    public String toString() {
        return "InkTimer[" +
                "runnable=" + runnable + ", " +
                "cancellationToken=" + cancellationToken + ", " +
                "delay=" + initialDelay + ", " +
                "delayRemain=" + delayRemain + ", " +
                "interval=" + initialInterval + ", " +
                "intervalRemain=" + intervalRemain + ']';
    }
}
