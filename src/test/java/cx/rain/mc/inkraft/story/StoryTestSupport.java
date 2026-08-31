package cx.rain.mc.inkraft.story;

import com.bladecoder.ink.runtime.Story;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.story.value.IStoryValue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StoryTestSupport {
    private static final Pattern EXTERNAL_CALL = Pattern.compile(
        "\"x\\(\\)\"\\s*:\\s*\"([A-Za-z_][A-Za-z0-9_]*)\"");
    private static final Story.ExternalFunction<Boolean> FALSE_EXTERNAL = _ -> false;

    public static String readStory(String name) throws IOException {
        var path = "/data/testmod/inkraft_story/" + name + ".ink.json";
        try (var stream = StoryTestSupport.class.getResourceAsStream(path)) {
            assertNotNull(stream, () -> "Missing compiled test story: " + path);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static void bindExternals(Story story, String json, ExternalOverride... overrides)
        throws Exception {
        var matcher = EXTERNAL_CALL.matcher(json);
        while (matcher.find()) {
            var name = matcher.group(1);
            if (story.getExternalFunction(name) != null) {
                continue;
            }

            Story.ExternalFunction<?> function = FALSE_EXTERNAL;
            for (var override : overrides) {
                if (override.name().equals(name)) {
                    function = override.function();
                    break;
                }
            }
            story.bindExternalFunction(name, function, false);
        }
    }

    public static void bindFunctions(Story story, IStoryFunction... functions) throws Exception {
        for (var function : functions) {
            story.bindExternalFunction(function.getName(), args -> {
                var values = new IStoryValue<?, ?>[args.length];
                for (int i = 0; i < args.length; i++) {
                    values[i] = IStoryValue.fromObject(args[i]);
                }
                return function.apply(null, values).asObject();
            }, function.isLookaheadSafe());
        }
    }

    public static ExternalOverride override(String name, Story.ExternalFunction<?> function) {
        return new ExternalOverride(name, function);
    }

    public record ExternalOverride(String name, Story.ExternalFunction<?> function) {
    }
}
