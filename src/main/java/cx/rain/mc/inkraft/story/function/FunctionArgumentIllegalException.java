package cx.rain.mc.inkraft.story.function;

public class FunctionArgumentIllegalException extends FunctionSyntaxException {
    public FunctionArgumentIllegalException(String message) {
        super(message);
    }

    public FunctionArgumentIllegalException(String message, Throwable cause) {
        super(message, cause);
    }

    public static FunctionArgumentIllegalException illegalArgument(String expected, String actual,
                                                                   Throwable cause) {
        return new FunctionArgumentIllegalException("Expected a valid " + expected + ", got \""
            + actual + "\".", cause);
    }
}
