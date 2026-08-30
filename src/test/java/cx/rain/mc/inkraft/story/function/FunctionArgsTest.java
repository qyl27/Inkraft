package cx.rain.mc.inkraft.story.function;

import cx.rain.mc.inkraft.story.value.ArrayStoryValue;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.FloatStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.StringStoryValue;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FunctionArgsTest {
    private static final IStoryValue<?, ?>[] EMPTY = {};
    private static final IStoryValue<?, ?>[] ONE_STRING = {new StringStoryValue("value")};

    @Test
    void validatesArgumentCounts() {
        assertDoesNotThrow(() -> FunctionArgs.expectCount(EMPTY, 0));
        assertDoesNotThrow(() -> FunctionArgs.expectCount(ONE_STRING, 1));
        assertThrows(FunctionArgumentCountException.class, () -> FunctionArgs.expectCount(EMPTY, 1));
        assertThrows(FunctionArgumentCountException.class, () -> FunctionArgs.expectCount(ONE_STRING, 0));
    }

    @Test
    void validatesArgumentIndexes() {
        assertDoesNotThrow(() -> FunctionArgs.expectMoreThan(ONE_STRING, 0));
        assertThrows(FunctionArgumentCountException.class, () -> FunctionArgs.expectMoreThan(EMPTY, 0));
        assertThrows(FunctionArgumentCountException.class, () -> FunctionArgs.expectMoreThan(ONE_STRING, 1));
    }

    @Test
    void readsTypedValuesWithGenericForms() {
        var string = new StringStoryValue("value");
        var integer = new IntStoryValue(12);
        var floating = new FloatStoryValue(2.5F);
        var bool = BoolStoryValue.TRUE;

        assertEquals(Optional.of("value"), FunctionArgs.getTyped(string, String.class));
        assertEquals(Optional.of("value"), FunctionArgs.getTyped(string, Object.class));
        assertEquals(Optional.of(12), FunctionArgs.getTyped(integer, Number.class));
        assertEquals(Optional.of(2.5F), FunctionArgs.getTyped(floating, Float.class));
        assertEquals(Optional.of(true), FunctionArgs.getTyped(bool, Boolean.class));
        assertEquals("value", FunctionArgs.requireTyped(string, String.class));
        assertEquals(12, FunctionArgs.requireTyped(integer, Number.class));
        assertEquals(2.5F, FunctionArgs.requireTyped(floating, Float.class));
        assertEquals(true, FunctionArgs.requireTyped(bool, Boolean.class));

        assertEquals(Optional.empty(), FunctionArgs.getTyped(integer, String.class));
        var exception = assertThrows(FunctionArgumentTypeException.class,
                () -> FunctionArgs.requireTyped(integer, String.class));
        assertEquals("Expected String, got Integer.", exception.getMessage());

        assertThrows(NullPointerException.class, () -> FunctionArgs.getTyped(null, String.class));
        assertThrows(NullPointerException.class, () -> FunctionArgs.getTyped(string, null));
    }

    @Test
    void createsArgumentExceptionsWithCentralizedDiagnostics() {
        var actual = new IntStoryValue(1);
        var typeException = FunctionArgumentTypeException.unexpectedType(String.class, actual);
        assertInstanceOf(FunctionArgumentTypeException.class, typeException);
        assertEquals("Expected String, got Integer.", typeException.getMessage());

        var cause = new IllegalArgumentException("Original cause");
        var illegalException = FunctionArgumentIllegalException.illegalArgument("UUID", "invalid", cause);
        assertInstanceOf(FunctionArgumentIllegalException.class, illegalException);
        assertEquals("Expected a valid UUID, got \"invalid\".", illegalException.getMessage());
        assertSame(cause, illegalException.getCause());
    }

    @Test
    void typedStringReadsUsePrimitiveRepresentation() {
        var array = ArrayStoryValue.empty();

        assertEquals(Optional.of("[]"), FunctionArgs.getTyped(array, String.class));
        assertEquals(Optional.of("[]"), FunctionArgs.getString(array));
        assertEquals("[]", FunctionArgs.requireTyped(array, String.class));
    }

    @Test
    void readsStringsWithOptionalAndRequiredForms() {
        var value = new StringStoryValue("value");
        var invalid = new IntStoryValue(1);

        assertEquals(Optional.of("value"), FunctionArgs.getString(value));
        assertEquals("value", FunctionArgs.requireString(value));
        assertEquals("value", FunctionArgs.getString(value).orElse("fallback"));

        assertEquals(Optional.empty(), FunctionArgs.getString(invalid));
        assertThrows(FunctionArgumentTypeException.class, () -> FunctionArgs.requireString(invalid));
        assertEquals("fallback", FunctionArgs.getString(invalid).orElse("fallback"));
    }

    @Test
    void readsIntsWithOptionalAndRequiredForms() {
        var value = new IntStoryValue(12);
        var invalid = new StringStoryValue("12");

        assertEquals(Optional.of(12), FunctionArgs.getInt(value));
        assertEquals(12, FunctionArgs.requireInt(value));
        assertEquals(12, FunctionArgs.getInt(value).orElse(3));

        assertEquals(Optional.empty(), FunctionArgs.getInt(invalid));
        assertThrows(FunctionArgumentTypeException.class, () -> FunctionArgs.requireInt(invalid));
        assertEquals(3, FunctionArgs.getInt(invalid).orElse(3));
        assertEquals(3, FunctionArgs.getInt(new StringStoryValue("")).orElse(3));
        assertEquals(3, FunctionArgs.getInt(BoolStoryValue.TRUE).orElse(3));
    }

    @Test
    void readsFloatsWithOptionalAndRequiredForms() {
        var value = new FloatStoryValue(2.5F);
        var invalid = new IntStoryValue(1);

        assertEquals(Optional.of(2.5F), FunctionArgs.getFloat(value));
        assertEquals(2.5F, FunctionArgs.requireFloat(value));
        assertEquals(2.5F, FunctionArgs.getFloat(value).orElse(1.5F));

        assertEquals(Optional.empty(), FunctionArgs.getFloat(invalid));
        assertThrows(FunctionArgumentTypeException.class, () -> FunctionArgs.requireFloat(invalid));
        assertEquals(1.5F, FunctionArgs.getFloat(invalid).orElse(1.5F));
    }

    @Test
    void readsBoolsWithOptionalAndRequiredForms() {
        var invalid = new IntStoryValue(1);

        assertEquals(Optional.of(true), FunctionArgs.getBool(BoolStoryValue.TRUE));
        assertEquals(Optional.of(false), FunctionArgs.getBool(BoolStoryValue.FALSE));
        assertEquals(true, FunctionArgs.requireBool(BoolStoryValue.TRUE));
        assertEquals(false, FunctionArgs.getBool(BoolStoryValue.FALSE).orElse(true));

        assertEquals(Optional.empty(), FunctionArgs.getBool(invalid));
        assertThrows(FunctionArgumentTypeException.class, () -> FunctionArgs.requireBool(invalid));
        assertEquals(true, FunctionArgs.getBool(invalid).orElse(true));
    }

    @Test
    void readsNonNegativeIntsAndIndexesWithOptionalAndRequiredForms() {
        var zero = new IntStoryValue(0);
        var positive = new IntStoryValue(3);
        var negative = new IntStoryValue(-1);
        var wrongType = new StringStoryValue("0");

        assertEquals(Optional.of(0), FunctionArgs.getNonNegativeInt(zero));
        assertEquals(Optional.of(3), FunctionArgs.getNonNegativeInt(positive));
        assertEquals(3, FunctionArgs.requireNonNegativeInt(positive));
        assertEquals(3, FunctionArgs.getNonNegativeInt(positive).orElse(8));
        assertEquals(Optional.empty(), FunctionArgs.getNonNegativeInt(negative));
        assertEquals(Optional.empty(), FunctionArgs.getNonNegativeInt(wrongType));
        assertThrows(FunctionArgumentRangeException.class, () -> FunctionArgs.requireNonNegativeInt(negative));
        assertThrows(FunctionArgumentTypeException.class, () -> FunctionArgs.requireNonNegativeInt(wrongType));
        assertEquals(-4, FunctionArgs.getNonNegativeInt(negative).orElse(-4));
        assertEquals(8, FunctionArgs.getNonNegativeInt(wrongType).orElse(8));

        var rangeException = assertThrows(FunctionArgumentRangeException.class,
                () -> FunctionArgs.requireNonNegativeInt(negative));
        assertInstanceOf(FunctionArgumentIllegalException.class, rangeException);
        assertInstanceOf(FunctionSyntaxException.class, rangeException);

        assertEquals(Optional.of(0), FunctionArgs.getIndex(zero));
        assertEquals(3, FunctionArgs.requireIndex(positive));
        assertEquals(3, FunctionArgs.getIndex(positive).orElse(8));
        assertEquals(Optional.empty(), FunctionArgs.getIndex(negative));
        assertEquals(Optional.empty(), FunctionArgs.getIndex(wrongType));
        assertThrows(FunctionArgumentRangeException.class, () -> FunctionArgs.requireIndex(negative));
        assertThrows(FunctionArgumentTypeException.class, () -> FunctionArgs.requireIndex(wrongType));
        assertEquals(-4, FunctionArgs.getIndex(negative).orElse(-4));
        assertEquals(8, FunctionArgs.getIndex(wrongType).orElse(8));
    }

    @Test
    void rejectsNullValuesInsteadOfTreatingThemAsParseFailures() {
        assertThrows(NullPointerException.class, () -> FunctionArgs.getString(null));
        assertThrows(NullPointerException.class, () -> FunctionArgs.getInt(null));
        assertThrows(NullPointerException.class, () -> FunctionArgs.getFloat(null));
        assertThrows(NullPointerException.class, () -> FunctionArgs.getBool(null));
        assertThrows(NullPointerException.class, () -> FunctionArgs.getNonNegativeInt(null));
        assertThrows(NullPointerException.class, () -> FunctionArgs.getIndex(null));
    }

    @Test
    void keepsRuntimeExceptionHierarchyMessagesAndCauses() {
        var exception = assertThrows(FunctionArgumentTypeException.class,
                () -> FunctionArgs.requireInt(new StringStoryValue("1")));
        assertInstanceOf(FunctionSyntaxException.class, exception);
        assertInstanceOf(RuntimeException.class, exception);

        var multiline = new FunctionArgumentRangeException("First line\nSecond line");
        assertEquals("First line\nSecond line", multiline.getMessage());

        var cause = new IllegalArgumentException("Original cause");
        var illegal = new FunctionArgumentIllegalException("Illegal argument", cause);
        assertInstanceOf(FunctionSyntaxException.class, illegal);
        assertInstanceOf(RuntimeException.class, illegal);
        assertEquals("Illegal argument", illegal.getMessage());
        assertSame(cause, illegal.getCause());
    }

}
