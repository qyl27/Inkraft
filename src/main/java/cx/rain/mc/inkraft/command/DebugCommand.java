package cx.rain.mc.inkraft.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.story.IStoryVariable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;

import static cx.rain.mc.inkraft.command.InkraftCommand.ARGUMENT_PLAYER;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class DebugCommand {
    public static final String ARGUMENT_NAME = "name";
    public static final String ARGUMENT_VALUE = "value";

    public static final LiteralArgumentBuilder<CommandSourceStack> INKRAFT_DEBUG = literal("debug")
            .requires(InkraftPlatform.getPermissionManager()::isAdmin)
            .then(literal("variables")
                    .then(literal("get")
                            .then(argument(ARGUMENT_NAME, StringArgumentType.word())
                                    .then(argument(ARGUMENT_PLAYER, EntityArgument.player())
                                            .executes(DebugCommand::onGetVariable))
                                    .executes(DebugCommand::onGetVariable)))
                    .then(literal("set")
                            .then(argument(ARGUMENT_NAME, StringArgumentType.word())
                                    .then(argument(ARGUMENT_VALUE, StringArgumentType.greedyString())
                                            .then(argument(ARGUMENT_PLAYER, EntityArgument.player())
                                                    .executes(DebugCommand::onGetVariable))
                                            .executes((DebugCommand::onSetVariable))))));

    private static int onGetVariable(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var name = StringArgumentType.getString(context, ARGUMENT_NAME);

        try {
            var object = EntityArgument.getPlayer(context, ARGUMENT_PLAYER);
            var data = InkraftPlatform.getPlayerData(object);
            var value = data.getVariable(name).getValue();
            player.sendSystemMessage(Component.literal("Variable " + name + ": " + value.toString()));   // Todo.
            return 1;
        } catch (CommandSyntaxException ex) {
            throw ex;
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        var data = InkraftPlatform.getPlayerData(player);
        var value = data.getVariable(name).getValue();
        player.sendSystemMessage(Component.literal("Variable " + name + ": " + value.toString()));   // Todo.

        return 1;
    }

    private static int onSetVariable(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var name = StringArgumentType.getString(context, ARGUMENT_NAME);
        var value = StringArgumentType.getString(context, ARGUMENT_VALUE);

        try {
            var object = EntityArgument.getPlayer(context, ARGUMENT_PLAYER);
            var data = InkraftPlatform.getPlayerData(object);
            data.setVariable(name, IStoryVariable.fromString(value));
            player.sendSystemMessage(Component.translatable(ModConstants.MESSAGE_COMMAND_SUCCESS)
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
            return 1;
        } catch (CommandSyntaxException ex) {
            throw ex;
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        var data = InkraftPlatform.getPlayerData(player);
        data.setVariable(name, IStoryVariable.fromString(value));
        player.sendSystemMessage(Component.translatable(ModConstants.MESSAGE_COMMAND_SUCCESS)
                .withStyle(ChatFormatting.LIGHT_PURPLE));

        return 1;
    }
}
