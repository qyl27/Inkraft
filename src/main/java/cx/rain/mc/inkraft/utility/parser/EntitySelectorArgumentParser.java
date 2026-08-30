package cx.rain.mc.inkraft.utility.parser;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.FunctionArgumentIllegalException;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;

import java.util.Optional;

public class EntitySelectorArgumentParser {
    public static <S> Optional<EntitySelector> getEntitySelector(IStoryValue<?, ?> value, EntityArgument argument,
                                                                 S source) {
        return FunctionArgs.getString(value).flatMap(string -> {
            try {
                return Optional.of(parseEntitySelector(string, argument, source));
            } catch (CommandSyntaxException ignored) {
                return Optional.empty();
            }
        });
    }

    public static <S> EntitySelector requireEntitySelector(IStoryValue<?, ?> value, EntityArgument argument,
                                                           S source) {
        var string = FunctionArgs.requireString(value);
        try {
            return parseEntitySelector(string, argument, source);
        } catch (CommandSyntaxException ex) {
            throw FunctionArgumentIllegalException.illegalArgument("entity selector", string, ex);
        }
    }

    private static <S> EntitySelector parseEntitySelector(String string, EntityArgument argument, S source)
        throws CommandSyntaxException {
        var reader = new StringReader(string);
        var selector = argument.parse(reader, source);
        if (reader.canRead()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(reader);
        }
        return selector;
    }

}
