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
                FunctionArgs.expectCount(args, 0);
                return ArrayStoryValue.empty();
            }
        };
    }

    public static AbstractLangFunction isArray() {
        return new AbstractLangFunction("isArray") {
            @Override
            public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // isArray(array)
                FunctionArgs.expectCount(args, 1);

                var value = FunctionArgs.requireString(args[0]);
                return BoolStoryValue.from(ArrayStoryValue.isValid(value));
            }
        };
    }

    public static AbstractLangFunction size() {
        return new AbstractLangFunction("arraySize") {
            @Override
            public IntStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // arraySize(array)
                FunctionArgs.expectCount(args, 1);

                var value = FunctionArgs.requireString(args[0]);
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
            public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // arraySet(array, index, value)
                FunctionArgs.expectCount(args, 3);

                var str = FunctionArgs.requireString(args[0]);
                return falseOnInvalidInput(() -> {
                    var index = FunctionArgs.requireInt(args[1]);
                    return ArrayStoryValue.parse(str).set(index, args[2]);
                });
            }
        };
    }

    public static AbstractLangFunction get() {
        return new AbstractLangFunction("arrayGet") {
            @Override
            public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // arrayGet(array, index)
                FunctionArgs.expectCount(args, 2);

                var str = FunctionArgs.requireString(args[0]);
                return falseOnInvalidInput(() -> {
                    var index = FunctionArgs.requireInt(args[1]);
                    return ArrayStoryValue.parse(str).get(index);
                });
            }
        };
    }

    public static AbstractLangFunction add() {
        return new AbstractLangFunction("arrayAdd") {
            @Override
            public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // arrayAdd(array, value)
                FunctionArgs.expectCount(args, 2);

                var str = FunctionArgs.requireString(args[0]);
                return falseOnInvalidInput(() -> ArrayStoryValue.parse(str).add(args[1]));
            }
        };
    }

    public static AbstractLangFunction remove() {
        return new AbstractLangFunction("arrayRemove") {
            @Override
            public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // arrayRemove(array, index)
                FunctionArgs.expectCount(args, 2);

                var str = FunctionArgs.requireString(args[0]);
                return falseOnInvalidInput(() -> {
                    var index = FunctionArgs.requireInt(args[1]);
                    return ArrayStoryValue.parse(str).remove(index);
                });
            }
        };
    }

    public static AbstractLangFunction contains() {
        return new AbstractLangFunction("arrayContains") {
            @Override
            public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // arrayRemove(array, value)
                FunctionArgs.expectCount(args, 2);

                var str = FunctionArgs.requireString(args[0]);
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
