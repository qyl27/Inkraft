package cx.rain.mc.inkraft.story.function.lang;

import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.value.*;
import cx.rain.mc.inkraft.story.StoryInstance;

public class ArrayFunctions {
    public static AbstractLangFunction create() {
        return new AbstractLangFunction("createArray") {
            @Override
            public ArrayStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // createArray()
                FunctionArgs.requireCount(args, 0);
                return ArrayStoryValue.empty();
            }
        };
    }

    public static AbstractLangFunction isArray() {
        return new AbstractLangFunction("isArray") {
            @Override
            public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // isArray(array)
                FunctionArgs.requireCount(args, 1);
                FunctionArgs.requireTyped(args, 0, String.class);

                var value = FunctionArgs.getString(args[0]);
                return BoolStoryValue.from(ArrayStoryValue.isValid(value));
            }
        };
    }

    public static AbstractLangFunction size() {
        return new AbstractLangFunction("arraySize") {
            @Override
            public IntStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // arraySize(array)
                FunctionArgs.requireCount(args, 1);
                FunctionArgs.requireTyped(args, 0, String.class);

                var value = FunctionArgs.getString(args[0]);
                try {
                    return new IntStoryValue(ArrayStoryValue.parse(value).size());
                } catch (IllegalArgumentException ignored) {
                    return new IntStoryValue(-1);
                }
            }
        };
    }

    public static AbstractLangFunction set() {
        return new AbstractLangFunction("arraySet") {
            @Override
            public ArrayStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // arraySet(array, index, value)
                FunctionArgs.requireCount(args, 3);
                FunctionArgs.requireTyped(args, 0, String.class);
                FunctionArgs.requireTyped(args, 1, Integer.class);

                var str = FunctionArgs.getString(args[0]);
                var array = ArrayStoryValue.parse(str);
                var index = FunctionArgs.getIndex(args[1]);
                return array.set(index, args[2]);
            }
        };
    }

    public static AbstractLangFunction get() {
        return new AbstractLangFunction("arrayGet") {
            @Override
            public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // arrayGet(array, index)
                FunctionArgs.requireCount(args, 2);
                FunctionArgs.requireTyped(args, 0, String.class);
                FunctionArgs.requireTyped(args, 1, Integer.class);

                var str = FunctionArgs.getString(args[0]);
                var array = ArrayStoryValue.parse(str);
                var index = FunctionArgs.getIndex(args[1]);
                return array.get(index);
            }
        };
    }

    public static AbstractLangFunction add() {
        return new AbstractLangFunction("arrayAdd") {
            @Override
            public ArrayStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // arrayAdd(array, value)
                FunctionArgs.requireCount(args, 2);
                FunctionArgs.requireTyped(args, 0, String.class);

                var str = FunctionArgs.getString(args[0]);
                var array = ArrayStoryValue.parse(str);
                return array.add(args[1]);
            }
        };
    }

    public static AbstractLangFunction remove() {
        return new AbstractLangFunction("arrayRemove") {
            @Override
            public ArrayStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // arrayRemove(array, index)
                FunctionArgs.requireCount(args, 2);
                FunctionArgs.requireTyped(args, 0, String.class);
                FunctionArgs.requireTyped(args, 1, Integer.class);

                var str = FunctionArgs.getString(args[0]);
                var array = ArrayStoryValue.parse(str);
                var index = FunctionArgs.getIndex(args[1]);
                return array.remove(index);
            }
        };
    }

    public static AbstractLangFunction contains() {
        return new AbstractLangFunction("arrayContains") {
            @Override
            public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // arrayRemove(array, value)
                FunctionArgs.requireCount(args, 2);
                FunctionArgs.requireTyped(args, 0, String.class);

                var str = FunctionArgs.getString(args[0]);
                try {
                    var array = ArrayStoryValue.parse(str);
                    return BoolStoryValue.from(array.contains(args[1]));
                } catch (IllegalArgumentException ignored) {
                    return BoolStoryValue.FALSE;
                }
            }
        };
    }
}
