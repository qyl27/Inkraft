package cx.rain.mc.inkraft.timer.cancellation;

public class CancellationToken implements ICancellationToken {
    private boolean cancelled = false;

    public CancellationToken() {
    }

    public void cancel() {
        cancelled = true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public static CancellationToken create() {
        return new CancellationToken();
    }
}
