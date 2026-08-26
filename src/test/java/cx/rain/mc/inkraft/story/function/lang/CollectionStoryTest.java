package cx.rain.mc.inkraft.story.function.lang;

import com.bladecoder.ink.runtime.Story;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CollectionStoryTest {
    private static final List<String> OTHER_EXTERNALS = List.of(
        "isDebug",
        "isInFlow", "flowTo", "newFlow", "removeFlow",
        "hasFlow", "isFlowEnded", "pause", "setLineTicks", "unsetLineTicks",
        "hasVariable", "getVariable", "setVariable", "unsetVariable", "clearVariables",
        "logDebug", "logInfo", "logWarn", "logError",
        "parseBool", "parseInt", "parseFloat", "toString",
        "getPlayerName", "getPlayerStat", "getFormattedPlayerStat",
        "getWorldDayTime", "getWorldGameTime", "getWorldDay", "getRealTime",
        "runCommand", "runUnlimitedCommand", "runSilentUnlimitedCommand", "runServerCommand",
        "getScoreboard", "setScoreboard", "addScoreboard", "subScoreboard", "multiplyScoreboard",
        "getStorage", "setStorage",
        "hasItem", "countItem", "giveItem", "takeItem"
    );

    @Test
    void compiledStoryRunsAcrossStateSaveAndRestore() throws Exception {
        var json = readStory();
        var story = createStory(json);

        var firstLine = story.Continue();
        var savedState = story.getState().toJson();

        var restored = createStory(json);
        restored.getState().loadJson(savedState);
        var output = firstLine + restored.continueMaximally();

        var expectedLines = List.of(
            "Inkraft 测试剧本六，JSON 数组和映射：",
            "新数组：[]",
            "arrayAdd：[1,2.0,true,\"1\"]",
            "arraySet & arrayRemove: [1,2.3,true]",
            "isArray：true",
            "isArrayEmpty：false",
            "arraySize：3",
            "arrayGet：2.3",
            "arrayContains：true",
            "arrayFirst：1",
            "arrayLast：true",
            "arrayHas: false",
            "isArray(\"[3, 2, 1]\"): true",
            "nesting arrayAdd：[1,2.3,true,\"[\\\"wolf\\\",\\\"seikou!\\\"]\"]",
            "nesting arrayGet：[\"wolf\",\"seikou!\"]",
            "新字典：{}",
            "mapSet：{\"a\":\"value\",\"b\":2.0}",
            "map is not an array：false",
            "array is not a map：false",
            "isMap：true",
            "mapSize：2",
            "mapGet：value",
            "mapRemove：{\"a\":\"value\"}",
            "mapContains：false",
            "nesting mapSet：{\"a\":\"value\",\"c\":\"[\\\"wolf\\\",\\\"seikou!\\\"]\"}",
            "nesting mapGet：[\"wolf\",\"seikou!\"]",
            "invalid arrayGet：false",
            "invalid arraySet：false",
            "invalid mapGet：false",
            "invalid mapRemove：false"
        );
        for (var expected : expectedLines) {
            assertTrue(output.contains(expected), () -> "Missing output: " + expected + '\n' + output);
        }
    }

    private static Story createStory(String json) throws Exception {
        var story = new Story(json);

        for (var function : List.of(
            ArrayFunctions.create(), ArrayFunctions.isArray(), ArrayFunctions.size(), ArrayFunctions.set(),
            ArrayFunctions.get(), ArrayFunctions.add(), ArrayFunctions.remove(), ArrayFunctions.contains())) {
            bind(story, function);
        }
        for (var function : List.of(
            MapFunctions.create(), MapFunctions.isMap(), MapFunctions.size(), MapFunctions.set(),
            MapFunctions.get(), MapFunctions.remove(), MapFunctions.contains())) {
            bind(story, function);
        }
        for (var name : OTHER_EXTERNALS) {
            story.bindExternalFunction(name, args -> false, false);
        }

        return story;
    }

    private static void bind(Story story, IStoryFunction function)
        throws Exception {
        story.bindExternalFunction(
            function.getName(),
            args -> {
                var variables = new IStoryValue<?, ?>[args.length];
                for (int i = 0; i < args.length; i++) {
                    variables[i] = IStoryValue.fromObject(args[i]);
                }
                return function.apply(null, variables).asObject();
            },
            function.isLookaheadSafe());
    }

    private static String readStory() throws IOException {
        try (var stream = CollectionStoryTest.class.getResourceAsStream(
            "/data/testmod/inkraft_story/test6.ink.json")) {
            assertNotNull(stream);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
