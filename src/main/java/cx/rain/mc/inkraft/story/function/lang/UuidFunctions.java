package cx.rain.mc.inkraft.story.function.lang;

import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.value.StringStoryValue;
import cx.rain.mc.inkraft.utility.parser.UuidArgumentParser;

import java.util.UUID;

public class UuidFunctions {
    public static AbstractLangFunction randomUuid() {
        return new AbstractLangFunction("randomUuid") {
            @Override
            public StringStoryValue apply(StoryInstance instance, IStoryValue<?, ?>... args) {
                // randomUuid()
                FunctionArgs.expectCount(args, 0);
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
                FunctionArgs.expectCount(args, 1);
                FunctionArgs.requireString(args[0]);

                return BoolStoryValue.from(UuidArgumentParser.getUuid(args[0]).isPresent());
            }
        };
    }
}
