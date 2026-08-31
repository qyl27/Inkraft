package cx.rain.mc.inkraft.story.function.lang;

import cx.rain.mc.inkraft.story.function.FunctionArgumentTypeException;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.StringStoryValue;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UuidFunctionTest {
    @Test
    void randomUuidReturnsCanonicalVersion4Uuid() {
        var function = UuidFunctions.randomUuid();
        var result = function.apply(null);
        var value = assertInstanceOf(StringStoryValue.class, result).getValue();
        var uuid = UUID.fromString(value);

        assertEquals(uuid.toString(), value);
        assertEquals(4, uuid.version());
        assertEquals(2, uuid.variant());
        assertFalse(function.isLookaheadSafe());
    }

    @Test
    void isUuidUsesJavaLenientParsingWithoutTrimming() {
        var function = UuidFunctions.isUuid();

        assertTrue(apply(function, "123e4567-e89b-12d3-a456-426614174000"));
        assertTrue(apply(function, "123E4567-E89B-12D3-A456-426614174000"));
        assertTrue(apply(function, "00000000-0000-0000-0000-000000000000"));
        assertTrue(apply(function, "1-1-1-1-1"));

        assertFalse(apply(function, ""));
        assertFalse(apply(function, " 123e4567-e89b-12d3-a456-426614174000"));
        assertFalse(apply(function, "123e4567-e89b-12d3-a456-426614174000 "));
        assertFalse(apply(function, "{123e4567-e89b-12d3-a456-426614174000}"));
        assertFalse(apply(function, "not-a-uuid"));
        assertTrue(function.isLookaheadSafe());
    }

    @Test
    void isUuidStillRejectsNonStringArguments() {
        var function = UuidFunctions.isUuid();

        assertThrows(FunctionArgumentTypeException.class,
            () -> function.apply(null, new IntStoryValue(1)));
    }

    private static boolean apply(AbstractLangFunction function, String value) {
        return assertInstanceOf(BoolStoryValue.class,
            function.apply(null, new StringStoryValue(value))).value();
    }
}
