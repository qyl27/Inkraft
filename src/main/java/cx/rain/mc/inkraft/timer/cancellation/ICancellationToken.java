package cx.rain.mc.inkraft.timer.cancellation;

@FunctionalInterface
public interface ICancellationToken {
    boolean isCancelled();
}
