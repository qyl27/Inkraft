package cx.rain.mc.inkraft.story.value;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContainerStoryValueTest {
    @Test
    void preservesScalarTypes() {
        var array = ArrayStoryValue.parse("[1,1.0,true,\"1\"]");

        assertInstanceOf(IntStoryValue.class, array.get(0));
        assertInstanceOf(FloatStoryValue.class, array.get(1));
        assertInstanceOf(BoolStoryValue.class, array.get(2));
        assertInstanceOf(StringStoryValue.class, array.get(3));
        assertEquals("[1,1.0,true,\"1\"]", array.toPrimitive());
        assertEquals(array.toPrimitive(), array.asObject());
        assertEquals(array.toPrimitive(), array.getString());
    }

    @Test
    void canonicalizesWhitespaceEscapesAndMapOrder() {
        var map = MapStoryValue.parse(" { \"雪\" : \"line\\n\\\"quote\\\"\", \"b\" : 2.0, \"a\" : 1 } ");

        assertEquals("{\"a\":1,\"b\":2.0,\"雪\":\"line\\n\\\"quote\\\"\"}", map.toPrimitive());
    }

    @Test
    void normalizesNullDuplicateKeysAndLossyNumbers() {
        var array = ArrayStoryValue.parse(
            "[null,2147483648,4294967296,1e100,-1e100,1e-100,-0.0]");

        assertEquals(BoolStoryValue.FALSE, array.get(0));
        assertEquals(new IntStoryValue(Integer.MIN_VALUE), array.get(1));
        assertEquals(IntStoryValue.ZERO, array.get(2));
        assertEquals(new FloatStoryValue(3.4E38F), array.get(3));
        assertEquals(new FloatStoryValue(-3.4E38F), array.get(4));
        assertEquals(FloatStoryValue.ZERO, array.get(5));
        assertEquals(FloatStoryValue.ZERO, array.get(6));
        assertEquals("[false,-2147483648,0,3.4E38,-3.4E38,0.0,0.0]", array.toPrimitive());

        var map = MapStoryValue.parse("{\"a\":1,\"a\":null,\"b\":1,\"b\":2}");
        assertEquals("{\"a\":false,\"b\":2}", map.toPrimitive());
        assertTrue(map.contains("a"));
        assertEquals(BoolStoryValue.FALSE, map.get("a"));
    }

    @Test
    void rejectsNonstandardJsonSyntax() {
        var invalidArrays = new String[]{
            "[1,]",
            "[/* comment */ 1]",
            "[1] trailing",
            "[NaN]",
            "[Infinity]",
            "[-Infinity]"
        };

        for (var source : invalidArrays) {
            assertFalse(ArrayStoryValue.isValid(source), source);
            assertThrows(IllegalArgumentException.class, () -> ArrayStoryValue.parse(source), source);
        }

        assertFalse(ArrayStoryValue.isValid(""));
        assertFalse(ArrayStoryValue.isValid("null"));
        assertFalse(MapStoryValue.isValid("null"));
    }

    @Test
    void distinguishesRootContainerKindsAndUsesCopyOnWrite() {
        assertTrue(ArrayStoryValue.isValid("[]"));
        assertFalse(ArrayStoryValue.isValid("{}"));
        assertTrue(MapStoryValue.isValid("{}"));
        assertFalse(MapStoryValue.isValid("[]"));

        var empty = ArrayStoryValue.empty();
        var populated = empty.add(new IntStoryValue(1));
        assertEquals("[]", empty.toPrimitive());
        assertEquals("[1]", populated.toPrimitive());
        assertThrows(UnsupportedOperationException.class,
            () -> populated.getValue().add(new IntStoryValue(2)));
    }

    @Test
    void flattensNestedJsonContainersToInkStrings() {
        var array = ArrayStoryValue.parse(
            "[[null],\"[false]\",{\"z\":[null],\"a\":1,\"a\":2.0}]");

        var nestedArray = assertInstanceOf(StringStoryValue.class, array.get(0));
        var encodedArray = assertInstanceOf(StringStoryValue.class, array.get(1));
        var nestedMap = assertInstanceOf(StringStoryValue.class, array.get(2));
        assertEquals(nestedArray, encodedArray);
        assertEquals("[false]", nestedArray.getValue());
        assertEquals("{\"a\":2.0,\"z\":\"[false]\"}", nestedMap.getValue());
        assertEquals("[\"[false]\",\"[false]\",\"{\\\"a\\\":2.0,\\\"z\\\":\\\"[false]\\\"}\"]",
            array.toPrimitive());

        var map = MapStoryValue.parse("{\"items\":[null]}");
        assertEquals("{\"items\":\"[false]\"}", map.toPrimitive());
        assertEquals(new StringStoryValue("[false]"), map.get("items"));
    }

    @Test
    void mutationsAlwaysStoreContainerValuesAsInkStrings() {
        var inner = ArrayStoryValue.empty().add(new IntStoryValue(1));
        var outer = ArrayStoryValue.empty().add(inner);

        assertEquals("[\"[1]\"]", outer.toPrimitive());
        assertEquals(new StringStoryValue("[1]"), outer.get(0));
        assertTrue(outer.contains(new StringStoryValue("[1]")));
        assertEquals(outer, ArrayStoryValue.parse("[[1]]"));

        var map = MapStoryValue.empty().set("items", inner);
        assertEquals("{\"items\":\"[1]\"}", map.toPrimitive());
        assertEquals(new StringStoryValue("[1]"), map.get("items"));
    }

    @Test
    void normalizesNonFiniteInkFloats() {
        var array = ArrayStoryValue.empty()
            .add(new FloatStoryValue(Float.NaN))
            .add(new FloatStoryValue(Float.POSITIVE_INFINITY))
            .add(new FloatStoryValue(Float.NEGATIVE_INFINITY))
            .add(new FloatStoryValue(-0.0F));

        assertEquals("[0.0,3.4E38,-3.4E38,0.0]", array.toPrimitive());
    }
}
