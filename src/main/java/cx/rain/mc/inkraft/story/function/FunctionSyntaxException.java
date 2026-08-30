package cx.rain.mc.inkraft.story.function;

public class FunctionSyntaxException extends RuntimeException {
    public FunctionSyntaxException(String message) {
        super(message);
    }

    public FunctionSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }
}
