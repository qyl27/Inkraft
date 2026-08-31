package cx.rain.mc.inkraft.story.function;

import cx.rain.mc.inkraft.story.value.ArrayStoryValue;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.FloatStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.StringStoryValue;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

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

    @TestFactory
    Stream<DynamicTest> readsPrimitiveValuesWithOptionalAndRequiredForms() {
        return Stream.of(
            access("string", new StringStoryValue("value"), "value",
                FunctionArgs::getString, FunctionArgs::requireString, new IntStoryValue(1)),
            access("int", new IntStoryValue(12), 12,
                FunctionArgs::getInt, FunctionArgs::requireInt,
                new StringStoryValue("12"), new StringStoryValue(""), BoolStoryValue.TRUE),
            access("float", new FloatStoryValue(2.5F), 2.5F,
                FunctionArgs::getFloat, FunctionArgs::requireFloat, new IntStoryValue(1)),
            access("bool", BoolStoryValue.FALSE, false,
                FunctionArgs::getBool, FunctionArgs::requireBool, new IntStoryValue(1))
        ).map(access -> DynamicTest.dynamicTest(access.name(), () -> {
            assertEquals(Optional.of(access.expected()), access.optional().apply(access.valid()));
            assertEquals(access.expected(), access.required().apply(access.valid()));
            for (var invalid : access.invalid()) {
                assertEquals(Optional.empty(), access.optional().apply(invalid));
                assertThrows(FunctionArgumentTypeException.class, () -> access.required().apply(invalid));
            }
        }));
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
        assertEquals(Optional.empty(), FunctionArgs.getNonNegativeInt(negative));
        assertEquals(Optional.empty(), FunctionArgs.getNonNegativeInt(wrongType));
        assertThrows(FunctionArgumentRangeException.class, () -> FunctionArgs.requireNonNegativeInt(negative));
        assertThrows(FunctionArgumentTypeException.class, () -> FunctionArgs.requireNonNegativeInt(wrongType));

        var rangeException = assertThrows(FunctionArgumentRangeException.class,
            () -> FunctionArgs.requireNonNegativeInt(negative));
        assertInstanceOf(FunctionArgumentIllegalException.class, rangeException);
        assertInstanceOf(FunctionSyntaxException.class, rangeException);

        assertEquals(FunctionArgs.getNonNegativeInt(zero), FunctionArgs.getIndex(zero));
        assertEquals(FunctionArgs.getNonNegativeInt(negative), FunctionArgs.getIndex(negative));
        assertEquals(3, FunctionArgs.requireIndex(positive));
        assertThrows(FunctionArgumentRangeException.class, () -> FunctionArgs.requireIndex(negative));
        assertThrows(FunctionArgumentTypeException.class, () -> FunctionArgs.requireIndex(wrongType));
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

    private static Access access(String name, IStoryValue<?, ?> valid, Object expected,
                                 Function<IStoryValue<?, ?>, Optional<?>> optional,
                                 Function<IStoryValue<?, ?>, Object> required,
                                 IStoryValue<?, ?>... invalid) {
        return new Access(name, valid, expected, optional, required, invalid);
    }

    private record Access(String name, IStoryValue<?, ?> valid, Object expected,
                          Function<IStoryValue<?, ?>, Optional<?>> optional,
                          Function<IStoryValue<?, ?>, Object> required,
                          IStoryValue<?, ?>[] invalid) {
    }

}
