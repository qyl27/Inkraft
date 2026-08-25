package cx.rain.mc.inkraft.story.function.lang;

import cx.rain.mc.inkraft.story.value.ArrayStoryValue;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.FloatStoryValue;
import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.value.MapStoryValue;
import cx.rain.mc.inkraft.story.value.StringStoryValue;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.story.function.FunctionArgumentTypeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollectionFunctionTest {
    @Test
    void arrayFunctionsUseCopyOnWriteAndTypedValues() {
        var create = ArrayFunctions.create();
        var add = ArrayFunctions.add();
        var set = ArrayFunctions.set();
        var get = ArrayFunctions.get();
        var remove = ArrayFunctions.remove();
        var size = ArrayFunctions.size();
        var contains = ArrayFunctions.contains();

        var empty = assertInstanceOf(ArrayStoryValue.class, apply(create)).toPrimitive();
        var withInt = assertInstanceOf(ArrayStoryValue.class, apply(add, empty, 1)).toPrimitive();
        var withFloat = assertInstanceOf(ArrayStoryValue.class, apply(add, withInt, 2.0F)).toPrimitive();
        var withBool = assertInstanceOf(ArrayStoryValue.class, apply(add, withFloat, true)).toPrimitive();
        var complete = assertInstanceOf(ArrayStoryValue.class, apply(add, withBool, "1")).toPrimitive();

        assertEquals("[]", empty);
        assertEquals("[1,2.0,true,\"1\"]", complete);
        assertEquals(4, assertInstanceOf(IntStoryValue.class, apply(size, complete)).value());
        assertEquals(1, assertInstanceOf(IntStoryValue.class, apply(get, complete, 0)).value());
        assertEquals(2.0F, assertInstanceOf(FloatStoryValue.class, apply(get, complete, 1)).value());
        assertTrue(assertInstanceOf(BoolStoryValue.class, apply(get, complete, 2)).value());
        assertEquals("1", assertInstanceOf(StringStoryValue.class, apply(get, complete, 3)).getValue());
        assertTrue(assertInstanceOf(BoolStoryValue.class, apply(contains, complete, 1)).value());
        assertTrue(assertInstanceOf(BoolStoryValue.class, apply(contains, complete, 2.0F)).value());
        assertTrue(assertInstanceOf(BoolStoryValue.class, apply(contains, complete, "1")).value());
        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(contains, complete, 1.0F)).value());

        var replaced = assertInstanceOf(ArrayStoryValue.class, apply(set, complete, 0, "first")).toPrimitive();
        assertEquals("[\"first\",2.0,true,\"1\"]", replaced);
        assertEquals("[1,2.0,true,\"1\"]", complete);

        var removed = assertInstanceOf(ArrayStoryValue.class, apply(remove, replaced, 1)).toPrimitive();
        assertEquals("[\"first\",true,\"1\"]", removed);
        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(get, removed, 3)).value());
        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(set, removed, 3, 0)).value());
        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(remove, removed, 3)).value());
        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(get, removed, -1)).value());
        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(set, removed, -1, 0)).value());
        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(remove, removed, -1)).value());
    }

    @Test
    void encodedAndNestedContainerInputsLookLikeInkStrings() {
        var add = ArrayFunctions.add();
        var get = ArrayFunctions.get();
        var is = ArrayFunctions.isArray();
        var contains = ArrayFunctions.contains();

        var outer = assertInstanceOf(ArrayStoryValue.class, apply(add, "[]", "[1]")).toPrimitive();
        var inner = assertInstanceOf(StringStoryValue.class, apply(get, outer, 0)).getValue();
        var nestedJsonInner = assertInstanceOf(StringStoryValue.class, apply(get, "[[1]]", 0)).getValue();

        assertEquals("[\"[1]\"]", outer);
        assertEquals("[1]", inner);
        assertEquals(inner, nestedJsonInner);
        assertTrue(assertInstanceOf(BoolStoryValue.class, apply(is, inner)).value());
        assertTrue(assertInstanceOf(BoolStoryValue.class, apply(contains, outer, "[1]")).value());
        assertTrue(assertInstanceOf(BoolStoryValue.class, apply(contains, "[[1]]", "[1]")).value());
    }

    @Test
    void arrayProbeFunctionsUseSentinels() {
        var is = ArrayFunctions.isArray();
        var size = ArrayFunctions.size();
        var set = ArrayFunctions.set();
        var get = ArrayFunctions.get();
        var add = ArrayFunctions.add();
        var remove = ArrayFunctions.remove();
        var contains = ArrayFunctions.contains();

        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(is, "invalid")).value());
        assertEquals(-1, assertInstanceOf(IntStoryValue.class, apply(size, "invalid")).value());
        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(set, "invalid", 0, 1)).value());
        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(get, "invalid", 0)).value());
        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(add, "invalid", 1)).value());
        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(remove, "invalid", 0)).value());
        assertTrue(assertInstanceOf(BoolStoryValue.class, apply(is, "[null]")).value());
        assertEquals(1, assertInstanceOf(IntStoryValue.class, apply(size, "[null]")).value());
        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(get, "[null]", 0)).value());
        assertTrue(assertInstanceOf(BoolStoryValue.class, apply(contains, "[null]", false)).value());
        assertTrue(is.isLookaheadSafe());
    }

    @Test
    void arrayFunctionsNormalizeNonFiniteInkFloats() {
        var add = ArrayFunctions.add();
        var set = ArrayFunctions.set();

        var withNaN = assertInstanceOf(ArrayStoryValue.class,
            apply(add, "[]", Float.NaN)).toPrimitive();
        var withPositiveInfinity = assertInstanceOf(ArrayStoryValue.class,
            apply(add, withNaN, Float.POSITIVE_INFINITY)).toPrimitive();
        var complete = assertInstanceOf(ArrayStoryValue.class,
            apply(add, withPositiveInfinity, Float.NEGATIVE_INFINITY)).toPrimitive();
        var replaced = assertInstanceOf(ArrayStoryValue.class,
            apply(set, complete, 1, Float.NaN)).toPrimitive();

        assertEquals("[0.0,3.4E38,-3.4E38]", complete);
        assertEquals("[0.0,0.0,-3.4E38]", replaced);
    }

    @Test
    void mapFunctionsSortKeysAndRequireStringKeys() {
        var set = MapFunctions.set();
        var get = MapFunctions.get();
        var remove = MapFunctions.remove();
        var contains = MapFunctions.contains();
        var size = MapFunctions.size();

        var withB = assertInstanceOf(MapStoryValue.class, apply(set, "{}", "b", 2.0F)).toPrimitive();
        var complete = assertInstanceOf(MapStoryValue.class, apply(set, withB, "a", "value")).toPrimitive();

        assertEquals("{\"a\":\"value\",\"b\":2.0}", complete);
        assertEquals(2, assertInstanceOf(IntStoryValue.class, apply(size, complete)).value());
        assertEquals("value", assertInstanceOf(StringStoryValue.class, apply(get, complete, "a")).getValue());
        assertTrue(assertInstanceOf(BoolStoryValue.class, apply(contains, complete, "b")).value());
        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(contains, complete, "missing")).value());
        assertEquals("[1]", assertInstanceOf(StringStoryValue.class,
            apply(get, "{\"items\":[1]}", "items")).getValue());
        assertTrue(assertInstanceOf(BoolStoryValue.class,
            apply(contains, "{\"missing\":null}", "missing")).value());
        assertFalse(assertInstanceOf(BoolStoryValue.class,
            apply(get, "{\"missing\":null}", "missing")).value());
        assertEquals(2, assertInstanceOf(IntStoryValue.class,
            apply(get, "{\"a\":1,\"a\":2}", "a")).value());

        var removed = assertInstanceOf(MapStoryValue.class, apply(remove, complete, "a")).toPrimitive();
        assertEquals("{\"b\":2.0}", removed);
        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(get, complete, "missing")).value());
        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(remove, complete, "missing")).value());
        assertThrows(FunctionArgumentTypeException.class, () -> apply(set, complete, 1, "value"));
    }

    @Test
    void mapProbeFunctionsUseSentinels() {
        var is = MapFunctions.isMap();
        var size = MapFunctions.size();
        var set = MapFunctions.set();
        var get = MapFunctions.get();
        var remove = MapFunctions.remove();
        var contains = MapFunctions.contains();

        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(is, "invalid")).value());
        assertEquals(-1, assertInstanceOf(IntStoryValue.class, apply(size, "invalid")).value());
        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(set, "invalid", "key", 1)).value());
        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(get, "invalid", "key")).value());
        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(remove, "invalid", "key")).value());
        assertFalse(assertInstanceOf(BoolStoryValue.class, apply(contains, "invalid", "key")).value());
        assertTrue(is.isLookaheadSafe());
    }

    private static IStoryValue<?, ?> apply(IStoryFunction function, Object... args) {
        var variables = new IStoryValue<?, ?>[args.length];
        for (int i = 0; i < args.length; i++) {
            variables[i] = IStoryValue.fromObject(args[i]);
        }
        return function.apply(null, variables);
    }
}
