package cx.rain.mc.inkraft.story.function;

import cx.rain.mc.inkraft.story.value.*;

public class FunctionArgs {
    public static void requireCount(IStoryValue<?, ?>[] args, int expected) {
        if (args.length != expected) {
            throw new IllegalArgumentException("Expected " + expected + " arguments, got " + args.length + '.');
        }
    }

    public static void requireMoreThan(IStoryValue<?, ?>[] args, int expected) {
        if (args.length <= expected) {
            throw new IllegalArgumentException("Expected more than " + expected + " arguments, got " + args.length + '.');
        }
    }

    public static void requireTyped(IStoryValue<?, ?>[] args, int index, Class<?> expected) {
        requireMoreThan(args, index);

        if (!expected.isAssignableFrom(args[index].getValueType())) {
            throw new IllegalArgumentException("Expected " + expected + " in the " + index + "th argument, got " + args[index].getClass() + '.');
        }
    }

    public static String getString(IStoryValue<?, ?> value) {
        if (value instanceof StringifyStoryValue<?> stringify) {
            return stringify.getString();
        }
        throw new UnsupportedOperationException();
    }

    public static int getInt(IStoryValue<?, ?> value) {
        if (value instanceof IntStoryValue(int i)) {
            return i;
        }
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    public static boolean getBool(IStoryValue<?, ?> value) {
        if (value instanceof BoolStoryValue b) {
            return b.value();
        }
        throw new UnsupportedOperationException();
    }

    public static int getIndex(IStoryValue<?, ?> value) {
        var index = getInt(value);
        if (index < 0) {
            throw new IllegalArgumentException("Index is negative: " + index);
        }
        return index;
    }
}
