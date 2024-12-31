package cx.rain.mc.inkraft.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.story.IStoryVariable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.CompletableFuture;

import static cx.rain.mc.inkraft.command.InkraftCommand.withOptionalPlayerArgs;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class VariablesCommand {
    public static final String ARGUMENT_NAME = "name";
    public static final String ARGUMENT_VALUE = "value";

    public static final LiteralArgumentBuilder<CommandSourceStack> INKRAFT_VARIABLES;

    static {
        INKRAFT_VARIABLES = literal("variables")
                .requires(InkraftPlatform.getPermissionManager()::isAdmin);

        var getArgs = argument(ARGUMENT_NAME, StringArgumentType.string())
                .suggests(VariablesCommand::suggestName)
                .executes(VariablesCommand::onGetVariable);
        var get = literal("get").then(getArgs);

        var setArgs = argument(ARGUMENT_NAME, StringArgumentType.string())
                .suggests(VariablesCommand::suggestName)
                .then(argument(ARGUMENT_VALUE, StringArgumentType.greedyString())
                        .executes(VariablesCommand::onSetVariable));
        var set = literal("set").then(setArgs);

        var unsetArgs = argument(ARGUMENT_NAME, StringArgumentType.string())
                .suggests(VariablesCommand::suggestName)
                .executes(VariablesCommand::onUnsetVariable);
        var unset = literal("unset").then(unsetArgs);

        INKRAFT_VARIABLES.then(withOptionalPlayerArgs(get, getArgs));
        INKRAFT_VARIABLES.then(withOptionalPlayerArgs(set, setArgs));
        INKRAFT_VARIABLES.then(withOptionalPlayerArgs(unset, unsetArgs));
    }

    private static CompletableFuture<Suggestions> suggestName(final CommandContext<CommandSourceStack> context,
                                                              final SuggestionsBuilder builder) throws CommandSyntaxException {
        var entity = context.getSource().getEntity();
        if (!(entity instanceof ServerPlayer)) {
            entity = context.getSource().getPlayerOrException();
        }
        var player = (ServerPlayer) entity;

        var data = InkraftPlatform.getPlayerData(player);
        for (var v : data.getVariables().keySet()) {
            builder.suggest(v);
        }
        return builder.buildFuture();
    }

    private static int onGetVariable(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var name = StringArgumentType.getString(context, ARGUMENT_NAME);

        var entity = context.getSource().getEntity();
        if (!(entity instanceof ServerPlayer)) {
            entity = context.getSource().getPlayerOrException();
        }
        var player = (ServerPlayer) entity;

        var data = InkraftPlatform.getPlayerData(player);
        if (!data.hasVariable(name)) {
            context.getSource().sendSuccess(() -> Component.translatable(ModConstants.Messages.COMMAND_VARIABLE_MISSING, name), true);
        } else {
            var value = data.getVariable(name).getValue();
            context.getSource().sendSuccess(() -> Component.translatable(ModConstants.Messages.COMMAND_VARIABLE_GET, name, value.toString()), true);
        }

        return 1;
    }

    private static int onSetVariable(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var entity = context.getSource().getEntity();
        if (!(entity instanceof ServerPlayer)) {
            entity = context.getSource().getPlayerOrException();
        }
        var player = (ServerPlayer) entity;

        var name = StringArgumentType.getString(context, ARGUMENT_NAME);
        var value = StringArgumentType.getString(context, ARGUMENT_VALUE);
        var data = InkraftPlatform.getPlayerData(player);
        data.setVariable(name, IStoryVariable.fromString(value));
        context.getSource().sendSuccess(() -> Component.translatable(ModConstants.Messages.COMMAND_VARIABLE_SET, name, value).withStyle(ChatFormatting.LIGHT_PURPLE), true);
        return 1;
    }

    private static int onUnsetVariable(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var entity = context.getSource().getEntity();
        if (!(entity instanceof ServerPlayer)) {
            entity = context.getSource().getPlayerOrException();
        }
        var player = (ServerPlayer) entity;

        var name = StringArgumentType.getString(context, ARGUMENT_NAME);
        var data = InkraftPlatform.getPlayerData(player);
        data.unsetVariable(name);
        context.getSource().sendSuccess(() -> Component.translatable(ModConstants.Messages.COMMAND_VARIABLE_UNSET, name).withStyle(ChatFormatting.LIGHT_PURPLE), true);
        return 1;
    }
}
