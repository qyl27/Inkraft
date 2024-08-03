package cx.rain.mc.inkraft.timer.cancellation;

public class CancellableToken implements ICancellationToken {
    private boolean cancelled = false;

    public CancellableToken() {
    }

    public void cancel() {
        cancelled = true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public static CancellableToken create() {
        return new CancellableToken();
    }
}
