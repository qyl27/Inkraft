package cx.rain.mc.inkraft.story.function;

import cx.rain.mc.inkraft.story.value.*;

public class FunctionArgs {
    public static void requireCount(IStoryValue<?, ?>[] args, int expected) {
        var actual = args.length;
        if (actual != expected) {
            throw new FunctionArgumentCountException("Expected " + expected + " arguments, got " + actual + '.');
        }
    }

    public static void requireMoreThan(IStoryValue<?, ?>[] args, int expected) {
        var actual = args.length;
        if (actual <= expected) {
            throw new FunctionArgumentCountException("Expected more than " + expected + " arguments, got " + actual + '.');
        }
    }

    public static void requireTyped(IStoryValue<?, ?>[] args, int index, Class<?> expected) {
        requireMoreThan(args, index);
        var value = args[index];

        if (!expected.isAssignableFrom(value.getValueType())) {
            throw new FunctionArgumentTypeException("Expected " + expected.getSimpleName() + " at argument " + index
                    + ", got " + getTypeName(value) + '.');
        }
    }

    public static String getString(IStoryValue<?, ?> value) {
        if (value instanceof StringifyStoryValue<?> stringify) {
            return stringify.getString();
        }
        throw unexpectedType(String.class, value);
    }

    public static int getInt(IStoryValue<?, ?> value) {
        if (value instanceof IntStoryValue(int i)) {
            return i;
        }
        throw unexpectedType(Integer.class, value);
    }

    public static int getIntOrDefault(IStoryValue<?, ?> value, int defaultValue) {
        if (value instanceof StringStoryValue string && string.getString().isEmpty()) {
            return defaultValue;
        }
        return getInt(value);
    }

    public static float getFloat(IStoryValue<?, ?> value) {
        if (value instanceof FloatStoryValue(float f)) {
            return f;
        }
        throw unexpectedType(Float.class, value);
    }

    public static boolean getBool(IStoryValue<?, ?> value) {
        if (value instanceof BoolStoryValue b) {
            return b.value();
        }
        throw unexpectedType(Boolean.class, value);
    }

    public static int getNonNegativeInt(IStoryValue<?, ?> value) {
        var result = getInt(value);
        if (result < 0) {
            throw new FunctionArgumentRangeException("Expected a non-negative integer, got " + result + '.');
        }
        return result;
    }

    public static int getIndex(IStoryValue<?, ?> value) {
        return getNonNegativeInt(value);
    }

    private static FunctionArgumentTypeException unexpectedType(Class<?> expected, IStoryValue<?, ?> actual) {
        return new FunctionArgumentTypeException("Expected " + expected.getSimpleName() + ", got "
                + getTypeName(actual) + '.');
    }

    private static String getTypeName(IStoryValue<?, ?> value) {
        return value.getValueType().getSimpleName();
    }
}
