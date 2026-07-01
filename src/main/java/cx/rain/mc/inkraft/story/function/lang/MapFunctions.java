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
                FunctionArgs.requireCount(args, 0);
                return MapStoryValue.empty();
            }
        };
    }

    public static AbstractLangFunction isMap() {
        return new AbstractLangFunction("isMap") {
            @Override
            public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // isMap(map)
                FunctionArgs.requireCount(args, 1);
                FunctionArgs.requireTyped(args, 0, String.class);

                var value = FunctionArgs.getString(args[0]);
                return BoolStoryValue.from(MapStoryValue.isValid(value));
            }
        };
    }

    public static AbstractLangFunction size() {
        return new AbstractLangFunction("mapSize") {
            @Override
            public IntStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // mapSize(map)
                FunctionArgs.requireCount(args, 1);
                FunctionArgs.requireTyped(args, 0, String.class);

                var value = FunctionArgs.getString(args[0]);
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
            public MapStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // mapSet(map, key, value)
                FunctionArgs.requireCount(args, 3);
                FunctionArgs.requireTyped(args, 0, String.class);
                FunctionArgs.requireTyped(args, 1, String.class);

                var str = FunctionArgs.getString(args[0]);
                var map = MapStoryValue.parse(str);
                var key = FunctionArgs.getString(args[1]);
                return map.set(key, args[2]);
            }
        };
    }

    public static AbstractLangFunction get() {
        return new AbstractLangFunction("mapGet") {
            @Override
            public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // mapGet(map, key)
                FunctionArgs.requireCount(args, 2);
                FunctionArgs.requireTyped(args, 0, String.class);
                FunctionArgs.requireTyped(args, 1, String.class);

                var str = FunctionArgs.getString(args[0]);
                var map = MapStoryValue.parse(str);
                var key = FunctionArgs.getString(args[1]);
                return map.get(key);
            }
        };
    }

    public static AbstractLangFunction remove() {
        return new AbstractLangFunction("mapRemove") {
            @Override
            public MapStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // mapRemove(map, key)
                FunctionArgs.requireCount(args, 2);
                FunctionArgs.requireTyped(args, 0, String.class);
                FunctionArgs.requireTyped(args, 1, String.class);

                var str = FunctionArgs.getString(args[0]);
                var map = MapStoryValue.parse(str);
                var key = FunctionArgs.getString(args[1]);
                return map.remove(key);
            }
        };
    }

    public static AbstractLangFunction contains() {
        return new AbstractLangFunction("mapContains") {
            @Override
            public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // mapContains(map, key)
                FunctionArgs.requireCount(args, 2);
                FunctionArgs.requireTyped(args, 0, String.class);
                FunctionArgs.requireTyped(args, 1, String.class);

                var str = FunctionArgs.getString(args[0]);
                var key = FunctionArgs.getString(args[1]);
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
