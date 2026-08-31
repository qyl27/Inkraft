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

        var empty = array(create);
        var withInt = array(add, empty, 1);
        var withFloat = array(add, withInt, 2.0F);
        var withBool = array(add, withFloat, true);
        var complete = array(add, withBool, "1");

        assertEquals("[]", empty);
        assertEquals("[1,2.0,true,\"1\"]", complete);
        assertEquals(4, integer(size, complete));
        assertEquals(1, integer(get, complete, 0));
        assertEquals(2.0F, floating(get, complete, 1));
        assertTrue(bool(get, complete, 2));
        assertEquals("1", string(get, complete, 3));
        assertTrue(bool(contains, complete, 1));
        assertTrue(bool(contains, complete, 2.0F));
        assertTrue(bool(contains, complete, "1"));
        assertFalse(bool(contains, complete, 1.0F));

        var replaced = array(set, complete, 0, "first");
        assertEquals("[\"first\",2.0,true,\"1\"]", replaced);
        assertEquals("[1,2.0,true,\"1\"]", complete);

        var removed = array(remove, replaced, 1);
        assertEquals("[\"first\",true,\"1\"]", removed);
        assertFalse(bool(get, removed, 3));
        assertFalse(bool(set, removed, 3, 0));
        assertFalse(bool(remove, removed, 3));
        assertFalse(bool(get, removed, -1));
        assertFalse(bool(set, removed, -1, 0));
        assertFalse(bool(remove, removed, -1));
    }

    @Test
    void encodedAndNestedContainerInputsLookLikeInkStrings() {
        var add = ArrayFunctions.add();
        var get = ArrayFunctions.get();
        var is = ArrayFunctions.isArray();
        var contains = ArrayFunctions.contains();

        var outer = array(add, "[]", "[1]");
        var inner = string(get, outer, 0);
        var nestedJsonInner = string(get, "[[1]]", 0);

        assertEquals("[\"[1]\"]", outer);
        assertEquals("[1]", inner);
        assertEquals(inner, nestedJsonInner);
        assertTrue(bool(is, inner));
        assertTrue(bool(contains, outer, "[1]"));
        assertTrue(bool(contains, "[[1]]", "[1]"));
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

        assertFalse(bool(is, "invalid"));
        assertEquals(-1, integer(size, "invalid"));
        assertFalse(bool(set, "invalid", 0, 1));
        assertFalse(bool(get, "invalid", 0));
        assertFalse(bool(add, "invalid", 1));
        assertFalse(bool(remove, "invalid", 0));
        assertTrue(bool(is, "[null]"));
        assertEquals(1, integer(size, "[null]"));
        assertFalse(bool(get, "[null]", 0));
        assertTrue(bool(contains, "[null]", false));
        assertTrue(is.isLookaheadSafe());
    }

    @Test
    void arrayFunctionsNormalizeNonFiniteInkFloats() {
        var add = ArrayFunctions.add();
        var set = ArrayFunctions.set();

        var withNaN = array(add, "[]", Float.NaN);
        var withPositiveInfinity = array(add, withNaN, Float.POSITIVE_INFINITY);
        var complete = array(add, withPositiveInfinity, Float.NEGATIVE_INFINITY);
        var replaced = array(set, complete, 1, Float.NaN);

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

        var withB = map(set, "{}", "b", 2.0F);
        var complete = map(set, withB, "a", "value");

        assertEquals("{\"a\":\"value\",\"b\":2.0}", complete);
        assertEquals(2, integer(size, complete));
        assertEquals("value", string(get, complete, "a"));
        assertTrue(bool(contains, complete, "b"));
        assertFalse(bool(contains, complete, "missing"));
        assertEquals("[1]", string(get, "{\"items\":[1]}", "items"));
        assertTrue(bool(contains, "{\"missing\":null}", "missing"));
        assertFalse(bool(get, "{\"missing\":null}", "missing"));
        assertEquals(2, integer(get, "{\"a\":1,\"a\":2}", "a"));

        var removed = map(remove, complete, "a");
        assertEquals("{\"b\":2.0}", removed);
        assertFalse(bool(get, complete, "missing"));
        assertFalse(bool(remove, complete, "missing"));
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

        assertFalse(bool(is, "invalid"));
        assertEquals(-1, integer(size, "invalid"));
        assertFalse(bool(set, "invalid", "key", 1));
        assertFalse(bool(get, "invalid", "key"));
        assertFalse(bool(remove, "invalid", "key"));
        assertFalse(bool(contains, "invalid", "key"));
        assertTrue(is.isLookaheadSafe());
    }

    private static String array(IStoryFunction function, Object... args) {
        return assertInstanceOf(ArrayStoryValue.class, apply(function, args)).toPrimitive();
    }

    private static String map(IStoryFunction function, Object... args) {
        return assertInstanceOf(MapStoryValue.class, apply(function, args)).toPrimitive();
    }

    private static boolean bool(IStoryFunction function, Object... args) {
        return assertInstanceOf(BoolStoryValue.class, apply(function, args)).value();
    }

    private static int integer(IStoryFunction function, Object... args) {
        return assertInstanceOf(IntStoryValue.class, apply(function, args)).value();
    }

    private static float floating(IStoryFunction function, Object... args) {
        return assertInstanceOf(FloatStoryValue.class, apply(function, args)).value();
    }

    private static String string(IStoryFunction function, Object... args) {
        return assertInstanceOf(StringStoryValue.class, apply(function, args)).getValue();
    }

    private static IStoryValue<?, ?> apply(IStoryFunction function, Object... args) {
        var variables = new IStoryValue<?, ?>[args.length];
        for (int i = 0; i < args.length; i++) {
            variables[i] = IStoryValue.fromObject(args[i]);
        }
        return function.apply(null, variables);
    }
}
