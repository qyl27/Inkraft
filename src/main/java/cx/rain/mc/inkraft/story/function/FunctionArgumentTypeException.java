package cx.rain.mc.inkraft.story.function;

import cx.rain.mc.inkraft.story.value.IStoryValue;

public class FunctionArgumentTypeException extends FunctionSyntaxException {
    public FunctionArgumentTypeException(String message) {
        super(message);
    }

    public static FunctionArgumentTypeException unexpectedType(Class<?> expected, IStoryValue<?, ?> actual) {
        return new FunctionArgumentTypeException("Expected " + expected.getSimpleName() + ", got "
            + actual.getValueType().getSimpleName() + '.');
    }
}
