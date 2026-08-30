package cx.rain.mc.inkraft.story.function.lang;

import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.value.MapStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;

public class MapFunctions {
    public static AbstractLangFunction create() {
        return new AbstractLangFunction("createMap") {
            @Override
            public MapStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // createMap()
                FunctionArgs.expectCount(args, 0);
                return MapStoryValue.empty();
            }
        };
    }

    public static AbstractLangFunction isMap() {
        return new AbstractLangFunction("isMap") {
            @Override
            public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // isMap(map)
                FunctionArgs.expectCount(args, 1);

                var value = FunctionArgs.requireString(args[0]);
                return BoolStoryValue.from(MapStoryValue.isValid(value));
            }
        };
    }

    public static AbstractLangFunction size() {
        return new AbstractLangFunction("mapSize") {
            @Override
            public IntStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // mapSize(map)
                FunctionArgs.expectCount(args, 1);

                var value = FunctionArgs.requireString(args[0]);
                try {
                    return new IntStoryValue(MapStoryValue.parse(value).size());
                } catch (IllegalArgumentException ignored) {
                    return new IntStoryValue(-1);
                }
            }
        };
    }

    public static AbstractLangFunction set() {
        return new AbstractLangFunction("mapSet") {
            @Override
            public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // mapSet(map, key, value)
                FunctionArgs.expectCount(args, 3);

                var str = FunctionArgs.requireString(args[0]);
                var key = FunctionArgs.requireString(args[1]);
                return falseOnInvalidInput(() -> MapStoryValue.parse(str).set(key, args[2]));
            }
        };
    }

    public static AbstractLangFunction get() {
        return new AbstractLangFunction("mapGet") {
            @Override
            public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // mapGet(map, key)
                FunctionArgs.expectCount(args, 2);

                var str = FunctionArgs.requireString(args[0]);
                var key = FunctionArgs.requireString(args[1]);
                return falseOnInvalidInput(() -> MapStoryValue.parse(str).get(key));
            }
        };
    }

    public static AbstractLangFunction remove() {
        return new AbstractLangFunction("mapRemove") {
            @Override
            public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // mapRemove(map, key)
                FunctionArgs.expectCount(args, 2);

                var str = FunctionArgs.requireString(args[0]);
                var key = FunctionArgs.requireString(args[1]);
                return falseOnInvalidInput(() -> MapStoryValue.parse(str).remove(key));
            }
        };
    }

    public static AbstractLangFunction contains() {
        return new AbstractLangFunction("mapContains") {
            @Override
            public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // mapContains(map, key)
                FunctionArgs.expectCount(args, 2);

                var str = FunctionArgs.requireString(args[0]);
                var key = FunctionArgs.requireString(args[1]);
                try {
                    var map = MapStoryValue.parse(str);
                    return BoolStoryValue.from(map.contains(key));
                } catch (IllegalArgumentException ignored) {
                    return BoolStoryValue.FALSE;
                }
            }
        };
    }
}
