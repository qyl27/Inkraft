package cx.rain.mc.inkraft.story.function.lang;

import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.value.StringStoryValue;

import java.util.UUID;

public class UuidFunctions {
    public static AbstractLangFunction randomUuid() {
        return new AbstractLangFunction("randomUuid") {
            @Override
            public StringStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // randomUuid()
                FunctionArgs.requireCount(args, 0);
                return new StringStoryValue(UUID.randomUUID().toString());
            }

            @Override
            public boolean isLookaheadSafe() {
                return false;
            }
        };
    }

    public static AbstractLangFunction isUuid() {
        return new AbstractLangFunction("isUuid") {
            @Override
            public BoolStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // isUuid(str)
                FunctionArgs.requireCount(args, 1);
                FunctionArgs.requireTyped(args, 0, String.class);

                try {
                    UUID.fromString(FunctionArgs.getString(args[0]));
                    return BoolStoryValue.TRUE;
                } catch (IllegalArgumentException ignored) {
                    return BoolStoryValue.FALSE;
                }
            }
        };
    }
}
