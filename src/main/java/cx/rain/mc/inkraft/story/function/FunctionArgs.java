package cx.rain.mc.inkraft.story.function;

import cx.rain.mc.inkraft.story.value.IStoryValue;

import java.util.Objects;
import java.util.Optional;

public class FunctionArgs {

    // region Args count assertion

    public static void expectCount(IStoryValue<?, ?>[] args, int expected) {
        var actual = args.length;
        if (actual != expected) {
            throw new FunctionArgumentCountException("Expected " + expected + " arguments, got " + actual + '.');
        }
    }

    public static void expectMoreThan(IStoryValue<?, ?>[] args, int expected) {
        var actual = args.length;
        if (actual <= expected) {
            throw new FunctionArgumentCountException("Expected more than " + expected + " arguments, got " + actual + '.');
        }
    }

    // endregion

    // region Primitive args

    public static <T> Optional<T> getTyped(IStoryValue<?, ?> value, Class<T> expected) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(expected, "expected");
        if (!expected.isAssignableFrom(value.getValueType())) {
            return Optional.empty();
        }
        return Optional.of(expected.cast(value.toPrimitive()));
    }

    public static <T> T requireTyped(IStoryValue<?, ?> value, Class<T> expected) {
        return getTyped(value, expected)
            .orElseThrow(() -> FunctionArgumentTypeException.unexpectedType(expected, value));
    }

    public static Optional<String> getString(IStoryValue<?, ?> value) {
        return getTyped(value, String.class);
    }

    public static String requireString(IStoryValue<?, ?> value) {
        return requireTyped(value, String.class);
    }

    public static Optional<Integer> getInt(IStoryValue<?, ?> value) {
        return getTyped(value, Integer.class);
    }

    public static int requireInt(IStoryValue<?, ?> value) {
        return requireTyped(value, Integer.class);
    }

    public static Optional<Float> getFloat(IStoryValue<?, ?> value) {
        return getTyped(value, Float.class);
    }

    public static float requireFloat(IStoryValue<?, ?> value) {
        return requireTyped(value, Float.class);
    }

    public static Optional<Boolean> getBool(IStoryValue<?, ?> value) {
        return getTyped(value, Boolean.class);
    }

    public static boolean requireBool(IStoryValue<?, ?> value) {
        return requireTyped(value, Boolean.class);
    }

    // endregion

    // region Extra args

    public static Optional<Integer> getNonNegativeInt(IStoryValue<?, ?> value) {
        return getInt(value).filter(result -> result >= 0);
    }

    public static int requireNonNegativeInt(IStoryValue<?, ?> value) {
        var result = requireInt(value);
        if (result < 0) {
            throw new FunctionArgumentRangeException("Expected a non-negative integer, got " + result + '.');
        }
        return result;
    }

    public static Optional<Integer> getIndex(IStoryValue<?, ?> value) {
        return getNonNegativeInt(value);
    }

    public static int requireIndex(IStoryValue<?, ?> value) {
        return requireNonNegativeInt(value);
    }

    // endregion
}
