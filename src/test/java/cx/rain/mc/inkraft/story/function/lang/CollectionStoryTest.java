package cx.rain.mc.inkraft.story.function.lang;

import com.bladecoder.ink.runtime.Story;
import cx.rain.mc.inkraft.story.StoryTestSupport;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CollectionStoryTest {
    @Test
    void compiledStoryRunsAcrossStateSaveAndRestore() throws Exception {
        var json = StoryTestSupport.readStory("test6");
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
        var actualLines = output.lines()
            .map(String::trim)
            .filter(line -> !line.isBlank())
            .toList();
        assertEquals(expectedLines, actualLines);
    }

    private static Story createStory(String json) throws Exception {
        var story = new Story(json);
        StoryTestSupport.bindFunctions(story,
            ArrayFunctions.create(), ArrayFunctions.isArray(), ArrayFunctions.size(), ArrayFunctions.set(),
            ArrayFunctions.get(), ArrayFunctions.add(), ArrayFunctions.remove(), ArrayFunctions.contains(),
            MapFunctions.create(), MapFunctions.isMap(), MapFunctions.size(), MapFunctions.set(),
            MapFunctions.get(), MapFunctions.remove(), MapFunctions.contains());
        StoryTestSupport.bindExternals(story, json);

        return story;
    }
}
