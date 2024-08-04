package cx.rain.mc.inkraft.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.story.IStoryVariable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;

import java.util.concurrent.CompletableFuture;

import static cx.rain.mc.inkraft.command.InkraftCommand.ARGUMENT_PLAYER;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class VariablesCommand {
    public static final String ARGUMENT_NAME = "name";
    public static final String ARGUMENT_VALUE = "value";

    public static final LiteralArgumentBuilder<CommandSourceStack> INKRAFT_VARIABLES = literal("variables")
            .requires(InkraftPlatform.getPermissionManager()::isAdmin)
            .then(literal("get")
                    .then(argument(ARGUMENT_PLAYER, EntityArgument.player())
                            .then(argument(ARGUMENT_NAME, StringArgumentType.word())
                                    .suggests(VariablesCommand::suggestName)
                                    .executes(VariablesCommand::onGetVariable)))
                    .then(argument(ARGUMENT_NAME, StringArgumentType.word())
                            .suggests(VariablesCommand::suggestName)
                            .executes(VariablesCommand::onGetVariable)))
            .then(literal("set")
                    .then(argument(ARGUMENT_PLAYER, EntityArgument.player())
                            .then(argument(ARGUMENT_NAME, StringArgumentType.word())
                                    .suggests(VariablesCommand::suggestName)
                                    .then(argument(ARGUMENT_VALUE, StringArgumentType.greedyString())
                                            .executes(VariablesCommand::onSetVariable))))
                    .then(argument(ARGUMENT_NAME, StringArgumentType.word())
                            .suggests(VariablesCommand::suggestName)
                            .then(argument(ARGUMENT_VALUE, StringArgumentType.greedyString())
                                    .executes(VariablesCommand::onSetVariable))));

    private static CompletableFuture<Suggestions> suggestName(final CommandContext<CommandSourceStack> context,
                                                               final SuggestionsBuilder builder) throws CommandSyntaxException {
        try {
            var object = EntityArgument.getPlayer(context, ARGUMENT_PLAYER);
            var data = InkraftPlatform.getPlayerData(object);
            for (var v : data.getVariables().keySet()) {
                builder.suggest(v);
            }
            return builder.buildFuture();
        } catch (IllegalStateException ignored) {
        }

        var player = context.getSource().getPlayerOrException();
        var data = InkraftPlatform.getPlayerData(player);
        for (var v : data.getVariables().keySet()) {
            builder.suggest(v);
        }
        return builder.buildFuture();
    }

    private static int onGetVariable(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var name = StringArgumentType.getString(context, ARGUMENT_NAME);

        try {
            var object = EntityArgument.getPlayer(context, ARGUMENT_PLAYER);
            var data = InkraftPlatform.getPlayerData(object);
            var value = data.getVariable(name).getValue();
            context.getSource().sendSuccess(() -> Component.translatable(ModConstants.Messages.COMMAND_VARIABLE, name, value.toString()), true);
            return 1;
        } catch (IllegalStateException ignored) {
        }

        var player = context.getSource().getPlayerOrException();
        var data = InkraftPlatform.getPlayerData(player);
        var value = data.getVariable(name).getValue();
        context.getSource().sendSuccess(() -> Component.translatable(ModConstants.Messages.COMMAND_VARIABLE, name, value.toString()), true);

        return 1;
    }

    private static int onSetVariable(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var name = StringArgumentType.getString(context, ARGUMENT_NAME);
        var value = StringArgumentType.getString(context, ARGUMENT_VALUE);

        try {
            var object = EntityArgument.getPlayer(context, ARGUMENT_PLAYER);
            var data = InkraftPlatform.getPlayerData(object);
            data.setVariable(name, IStoryVariable.fromString(value));
            context.getSource().sendSuccess(() -> Component.translatable(ModConstants.Messages.COMMAND_SUCCESS).withStyle(ChatFormatting.LIGHT_PURPLE), true);
            return 1;
        } catch (IllegalStateException ignored) {
        }

        var player = context.getSource().getPlayerOrException();
        var data = InkraftPlatform.getPlayerData(player);
        data.setVariable(name, IStoryVariable.fromString(value));
        context.getSource().sendSuccess(() -> Component.translatable(ModConstants.Messages.COMMAND_SUCCESS).withStyle(ChatFormatting.LIGHT_PURPLE), true);
        return 1;
    }
}
