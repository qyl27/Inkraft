package cx.rain.mc.inkraft.utility.parser;

import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.FunctionArgumentIllegalException;
import cx.rain.mc.inkraft.story.value.IStoryValue;

import java.util.Optional;
import java.util.UUID;

public class UuidArgumentParser {
    public static Optional<UUID> getUuid(IStoryValue<?, ?> value) {
        return FunctionArgs.getString(value).flatMap(string -> {
            try {
                return Optional.of(UUID.fromString(string));
            } catch (IllegalArgumentException ignored) {
                return Optional.empty();
            }
        });
    }

    public static UUID requireUuid(IStoryValue<?, ?> value) {
        var string = FunctionArgs.requireString(value);
        try {
            return UUID.fromString(string);
        } catch (IllegalArgumentException ex) {
            throw FunctionArgumentIllegalException.illegalArgument("UUID", string, ex);
        }
    }
}
