package cx.rain.mc.inkraft.storage;

import com.bladecoder.ink.runtime.InkList;
import com.bladecoder.ink.runtime.InkListItem;
import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.story.value.ArrayStoryValue;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.FloatStoryValue;
import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.InkListStoryValue;
import cx.rain.mc.inkraft.story.value.MapStoryValue;
import cx.rain.mc.inkraft.story.value.StringStoryValue;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueOutput;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class InkPlayerDataTest {
    private static final HolderLookup.Provider EMPTY_LOOKUP = HolderLookup.Provider.create(Stream.empty());

    @Test
    void roundTripsTypedVariablesWithoutStringAmbiguity() {
        var data = new InkPlayerData();
        data.setPendingLine(ModConstants.Values.DEFAULT_FLOW_NAME, true);
        data.setVariable("bool", BoolStoryValue.TRUE);
        data.setVariable("int", new IntStoryValue(12));
        data.setVariable("float", new FloatStoryValue(2.5F));
        data.setVariable("string_bool", new StringStoryValue("true"));
        data.setVariable("string_int", new StringStoryValue("1"));
        data.setVariable("string_float", new StringStoryValue("1.0"));
        data.setVariable("json", new StringStoryValue("[1]"));
        data.setVariable(ModConstants.Variables.LINE_PAUSE_TICKS, new IntStoryValue(20));

        var restored = roundTrip(data);

        assertTrue(restored.hasPendingLine(ModConstants.Values.DEFAULT_FLOW_NAME));
        assertSame(BoolStoryValue.TRUE, restored.getVariable("bool"));
        assertEquals(new IntStoryValue(12), restored.getVariable("int"));
        assertEquals(new FloatStoryValue(2.5F), restored.getVariable("float"));
        assertEquals(new StringStoryValue("true"), restored.getVariable("string_bool"));
        assertEquals(new StringStoryValue("1"), restored.getVariable("string_int"));
        assertEquals(new StringStoryValue("1.0"), restored.getVariable("string_float"));
        assertEquals(new StringStoryValue("[1]"), restored.getVariable("json"));
        assertEquals(20, restored.getVariable(ModConstants.Variables.LINE_PAUSE_TICKS, IntStoryValue.class));
    }

    @Test
    void normalizesInkListsAndJsonContainersWhenSet() {
        var list = new InkList();
        list.put(new InkListItem("mood", "happy"), 1);
        list.put(new InkListItem("mood", "sad"), 2);

        var data = new InkPlayerData();
        data.setVariable("list", new InkListStoryValue(list));
        data.setVariable("array", ArrayStoryValue.parse("[1]"));
        data.setVariable("map", MapStoryValue.parse("{\"b\":2,\"a\":1}"));

        assertEquals(new StringStoryValue("happy, sad"), data.getVariable("list"));
        assertEquals(new StringStoryValue("[1]"), data.getVariable("array"));
        assertEquals(new StringStoryValue("{\"a\":1,\"b\":2}"), data.getVariable("map"));

        var restored = roundTrip(data);
        assertEquals(data.getVariables(), restored.getVariables());
    }

    @Test
    void migratesLegacyStringVariablesUsingExistingInference() {
        var output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        var variables = output.childrenList(ModConstants.Tags.VARIABLES);
        addLegacyVariable(variables, "bool", "true");
        addLegacyVariable(variables, "int", "1");
        addLegacyVariable(variables, "float", "1.0");
        addLegacyVariable(variables, "string", "value");

        var restored = read(output.buildResult());

        assertSame(BoolStoryValue.TRUE, restored.getVariable("bool"));
        assertEquals(new IntStoryValue(1), restored.getVariable("int"));
        assertEquals(new FloatStoryValue(1.0F), restored.getVariable("float"));
        assertEquals(new StringStoryValue("value"), restored.getVariable("string"));
    }

    @Test
    void skipsUnknownAndMalformedValuesWithoutDroppingValidVariables() {
        var data = new InkPlayerData();
        data.setVariable("valid", new IntStoryValue(3));
        data.setVariable("unknown", new IntStoryValue(4));
        data.setVariable("malformed", new IntStoryValue(5));

        var output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        data.serialize(output);

        var root = output.buildResult();
        var unknown = findStoredVariable(root, "unknown")
            .getCompoundOrEmpty(ModConstants.Tags.VARIABLE_ITEM_VALUE);
        unknown.putString("type", "future");
        var malformed = findStoredVariable(root, "malformed")
            .getCompoundOrEmpty(ModConstants.Tags.VARIABLE_ITEM_VALUE);
        malformed.put("value", StringTag.valueOf("not an int"));

        var restored = read(root);

        assertEquals(new IntStoryValue(3), restored.getVariable("valid"));
        assertFalse(restored.hasVariable("unknown"));
        assertFalse(restored.hasVariable("malformed"));
    }

    @Test
    void defaultsMissingPendingLineToNoPending() {
        var output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        output.putString(ModConstants.Tags.STORY, "testmod:test");
        output.putString(ModConstants.Tags.STATE, "{}");
        output.putBoolean(ModConstants.Tags.ENDED, false);

        var restored = read(output.buildResult());

        assertFalse(restored.isEnded());
        assertFalse(restored.hasPendingLine(ModConstants.Values.DEFAULT_FLOW_NAME));
    }

    @Test
    void roundTripsPendingLines() {
        var data = new InkPlayerData();
        data.setPendingLine(ModConstants.Values.DEFAULT_FLOW_NAME, false);
        data.setPendingLine("side", true);

        var restored = roundTrip(data);

        assertFalse(restored.hasPendingLine(ModConstants.Values.DEFAULT_FLOW_NAME));
        assertTrue(restored.hasPendingLine("side"));
        assertFalse(restored.hasPendingLine("missing"));
    }

    @Test
    void dropsFalsePendingLineEntriesWhenReserialized() {
        var root = new CompoundTag();
        var pendingLines = new CompoundTag();
        pendingLines.putBoolean("active", true);
        pendingLines.putBoolean("inactive", false);
        root.put(ModConstants.Tags.PENDING_LINES, pendingLines);

        var restored = read(root);

        assertTrue(restored.hasPendingLine("active"));
        assertFalse(restored.hasPendingLine("inactive"));

        var output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        restored.serialize(output);
        var serialized = output.buildResult().toString();

        assertTrue(serialized.contains("active"));
        assertFalse(serialized.contains("inactive"));
    }

    @Test
    void serializesPendingLineTagNames() {
        var data = new InkPlayerData();
        data.setPendingLine("side", true);

        var output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        data.serialize(output);

        var serialized = output.buildResult().toString();

        assertTrue(serialized.contains(ModConstants.Tags.PENDING_LINES));
        assertTrue(serialized.contains("side"));
        assertTrue(serialized.contains("1b"));
        assertFalse(serialized.contains("flow"));
        assertFalse(serialized.contains("line_pending_by_flow"));
        assertFalse(serialized.contains("pending_line_by_flow"));
        assertFalse(serialized.contains("current_line_displayed"));
        assertFalse(serialized.contains("flow_line_displayed"));
        assertFalse(serialized.contains("displayed"));
    }

    @Test
    void resetClearsPendingLines() {
        var data = new InkPlayerData();
        data.setPendingLine(ModConstants.Values.DEFAULT_FLOW_NAME, true);
        data.setPendingLine("side", true);

        data.resetState();

        assertTrue(data.isEnded());
        assertFalse(data.hasPendingLine(ModConstants.Values.DEFAULT_FLOW_NAME));
        assertFalse(data.hasPendingLine("side"));
    }

    @Test
    void roundTripCopiesPersistentDataAndDropsTransientState() {
        var source = new InkPlayerData();
        source.setStory(Identifier.fromNamespaceAndPath("testmod", "source"));
        source.setState("{\"state\":\"source\"}");
        source.setEnded(false);
        source.setPendingLine("source_flow", true);
        source.setVariable("source_variable", new IntStoryValue(17));
        var sourceToken = UUID.randomUUID();
        source.setContinuousToken(sourceToken);

        var target = new InkPlayerData();
        target.setStory(Identifier.fromNamespaceAndPath("testmod", "stale"));
        target.setState("stale-state");
        target.setPendingLine("stale_flow", true);
        target.setVariable("stale_variable", new StringStoryValue("stale"));
        target.setContinuousToken(UUID.randomUUID());

        roundTripInto(source, target);

        assertEquals(source.getStory(), target.getStory());
        assertEquals(source.getState(), target.getState());
        assertEquals(source.isEnded(), target.isEnded());
        assertTrue(target.hasPendingLine("source_flow"));
        assertFalse(target.hasPendingLine("stale_flow"));
        assertEquals(new IntStoryValue(17), target.getVariable("source_variable"));
        assertFalse(target.hasVariable("stale_variable"));
        assertNull(target.getContinuousToken());
        assertEquals(sourceToken, source.getContinuousToken());

        target.removePendingLine("source_flow");
        target.unsetVariable("source_variable");
        assertTrue(source.hasPendingLine("source_flow"));
        assertTrue(source.hasVariable("source_variable"));
    }

    private static InkPlayerData roundTrip(InkPlayerData data) {
        var restored = new InkPlayerData();
        roundTripInto(data, restored);
        return restored;
    }

    private static void roundTripInto(InkPlayerData source, InkPlayerData target) {
        var output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        source.serialize(output);
        target.deserialize(TagValueInput.create(ProblemReporter.DISCARDING, EMPTY_LOOKUP,
            output.buildResult()));
    }

    private static InkPlayerData read(CompoundTag tag) {
        var data = new InkPlayerData();
        data.deserialize(TagValueInput.create(ProblemReporter.DISCARDING, EMPTY_LOOKUP, tag));
        return data;
    }

    private static CompoundTag findStoredVariable(CompoundTag root, String name) {
        var variables = root.getListOrEmpty(ModConstants.Tags.VARIABLES);
        for (int i = 0; i < variables.size(); i++) {
            var variable = variables.getCompoundOrEmpty(i);
            if (name.equals(variable.getStringOr(ModConstants.Tags.VARIABLE_ITEM_NAME, ""))) {
                return variable;
            }
        }

        return fail("Missing serialized variable: " + name);
    }

    private static void addLegacyVariable(ValueOutput.ValueOutputList variables,
                                          String name,
                                          String value) {
        var item = variables.addChild();
        item.putString(ModConstants.Tags.VARIABLE_ITEM_NAME, name);
        item.putString(ModConstants.Tags.VARIABLE_ITEM_VALUE, value);
    }

}
