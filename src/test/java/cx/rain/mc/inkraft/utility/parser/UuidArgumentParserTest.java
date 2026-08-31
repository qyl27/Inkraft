package cx.rain.mc.inkraft.utility.parser;

import cx.rain.mc.inkraft.story.function.FunctionArgumentIllegalException;
import cx.rain.mc.inkraft.story.function.FunctionArgumentTypeException;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.StringStoryValue;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UuidArgumentParserTest {
    @Test
    void readsUuidsUsingJavaSemanticsWithoutTrimming() {
        var canonical = new StringStoryValue("123e4567-e89b-12d3-a456-426614174000");
        var shortSegments = new StringStoryValue("1-1-1-1-1");
        var expectedCanonical = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        var expectedShort = UUID.fromString("00000001-0001-0001-0001-000000000001");
        var fallback = UUID.fromString("3c2f503d-65cc-4ff3-aa2c-6a1312fd1da7");

        assertEquals(Optional.of(expectedCanonical), UuidArgumentParser.getUuid(canonical));
        assertEquals(expectedCanonical, UuidArgumentParser.requireUuid(canonical));
        assertEquals(expectedCanonical, UuidArgumentParser.getUuid(canonical).orElse(fallback));
        assertEquals(Optional.of(expectedShort), UuidArgumentParser.getUuid(shortSegments));
        assertEquals(expectedShort, UuidArgumentParser.requireUuid(shortSegments));

        assertInvalidUuid(new StringStoryValue(""), fallback);
        assertInvalidUuid(new StringStoryValue(" 123e4567-e89b-12d3-a456-426614174000"), fallback);
        assertInvalidUuid(new StringStoryValue("123e4567-e89b-12d3-a456-426614174000 "), fallback);
        assertInvalidUuid(new StringStoryValue("not-a-uuid"), fallback);

        var wrongType = new IntStoryValue(1);
        assertEquals(Optional.empty(), UuidArgumentParser.getUuid(wrongType));
        assertThrows(FunctionArgumentTypeException.class, () -> UuidArgumentParser.requireUuid(wrongType));
        assertSame(fallback, UuidArgumentParser.getUuid(wrongType).orElse(fallback));
    }

    @Test
    void rejectsNullValuesInsteadOfTreatingThemAsParseFailures() {
        assertThrows(NullPointerException.class, () -> UuidArgumentParser.getUuid(null));
        assertThrows(NullPointerException.class, () -> UuidArgumentParser.requireUuid(null));
    }

    private static void assertInvalidUuid(IStoryValue<?, ?> value, UUID fallback) {
        assertEquals(Optional.empty(), UuidArgumentParser.getUuid(value));
        var exception = assertThrows(FunctionArgumentIllegalException.class,
            () -> UuidArgumentParser.requireUuid(value));
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        assertSame(fallback, UuidArgumentParser.getUuid(value).orElse(fallback));
    }
}
