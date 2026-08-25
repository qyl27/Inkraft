package cx.rain.mc.inkraft.story.function;

import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.FloatStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.StringStoryValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FunctionArgsTest {
    private static final IStoryValue<?, ?>[] EMPTY = {};
    private static final IStoryValue<?, ?>[] ONE_STRING = {new StringStoryValue("value")};

    @Test
    void validatesArgumentCounts() {
        assertDoesNotThrow(() -> FunctionArgs.requireCount(EMPTY, 0));
        assertDoesNotThrow(() -> FunctionArgs.requireCount(ONE_STRING, 1));
        assertThrows(FunctionArgumentCountException.class, () -> FunctionArgs.requireCount(EMPTY, 1));
        assertThrows(FunctionArgumentCountException.class, () -> FunctionArgs.requireCount(ONE_STRING, 0));
    }

    @Test
    void validatesArgumentIndexes() {
        assertDoesNotThrow(() -> FunctionArgs.requireMoreThan(ONE_STRING, 0));
        assertThrows(FunctionArgumentCountException.class, () -> FunctionArgs.requireMoreThan(EMPTY, 0));
        assertThrows(FunctionArgumentCountException.class, () -> FunctionArgs.requireMoreThan(ONE_STRING, 1));
    }

    @Test
    void validatesArgumentTypes() {
        assertDoesNotThrow(() -> FunctionArgs.requireTyped(ONE_STRING, 0, String.class));
        assertDoesNotThrow(() -> FunctionArgs.requireTyped(ONE_STRING, 0, Object.class));
        assertThrows(FunctionArgumentTypeException.class,
            () -> FunctionArgs.requireTyped(ONE_STRING, 0, Integer.class));
        assertThrows(FunctionArgumentCountException.class,
            () -> FunctionArgs.requireTyped(EMPTY, 0, String.class));
    }

    @Test
    void extractsTypedValues() {
        assertEquals("value", FunctionArgs.getString(new StringStoryValue("value")));
        assertEquals(12, FunctionArgs.getInt(new IntStoryValue(12)));
        assertEquals(2.5F, FunctionArgs.getFloat(new FloatStoryValue(2.5F)));
        assertTrue(FunctionArgs.getBool(BoolStoryValue.TRUE));
        assertFalse(FunctionArgs.getBool(BoolStoryValue.FALSE));

        assertThrows(FunctionArgumentTypeException.class,
            () -> FunctionArgs.getString(new IntStoryValue(1)));
        assertThrows(FunctionArgumentTypeException.class,
            () -> FunctionArgs.getInt(new StringStoryValue("1")));
        assertThrows(FunctionArgumentTypeException.class,
            () -> FunctionArgs.getFloat(new IntStoryValue(1)));
        assertThrows(FunctionArgumentTypeException.class,
            () -> FunctionArgs.getBool(new IntStoryValue(1)));
    }

    @Test
    void extractsOptionalInts() {
        assertEquals(3, FunctionArgs.getIntOrDefault(new IntStoryValue(3), 1));
        assertEquals(1, FunctionArgs.getIntOrDefault(new StringStoryValue(""), 1));
        assertThrows(FunctionArgumentTypeException.class,
            () -> FunctionArgs.getIntOrDefault(new StringStoryValue("3"), 1));
    }

    @Test
    void validatesNonNegativeIntsAndIndexes() {
        assertEquals(0, FunctionArgs.getNonNegativeInt(new IntStoryValue(0)));
        assertEquals(3, FunctionArgs.getNonNegativeInt(new IntStoryValue(3)));
        assertEquals(0, FunctionArgs.getIndex(new IntStoryValue(0)));
        assertThrows(FunctionArgumentRangeException.class,
            () -> FunctionArgs.getNonNegativeInt(new IntStoryValue(-1)));
        assertThrows(FunctionArgumentRangeException.class,
            () -> FunctionArgs.getIndex(new IntStoryValue(-1)));
        assertThrows(FunctionArgumentTypeException.class,
            () -> FunctionArgs.getNonNegativeInt(new StringStoryValue("0")));
        assertThrows(FunctionArgumentTypeException.class,
            () -> FunctionArgs.getIndex(new StringStoryValue("0")));
    }

    @Test
    void keepsRuntimeExceptionHierarchyAndOriginalMessages() {
        var exception = assertThrows(FunctionArgumentTypeException.class,
            () -> FunctionArgs.getInt(new StringStoryValue("1")));
        assertInstanceOf(FunctionSyntaxException.class, exception);
        assertInstanceOf(RuntimeException.class, exception);

        var multiline = new FunctionArgumentRangeException("First line\nSecond line");
        assertEquals("First line\nSecond line", multiline.getMessage());
    }
}
