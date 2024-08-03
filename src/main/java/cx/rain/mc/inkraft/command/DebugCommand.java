package cx.rain.mc.inkraft.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.story.IStoryVariable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class DebugCommand {
    public static final LiteralArgumentBuilder<CommandSourceStack>  INKRAFT_DEBUG = literal("debug")
            .requires(InkraftPlatform.getPermissionManager()::hasClearPermission)
            .then(literal("variables")
                    .then(literal("get")
                            .then(argument("name", StringArgumentType.word())
                                    .executes(DebugCommand::onGetVariable)))
                    .then(literal("set")
                            .then(argument("name", StringArgumentType.word())
                                    .then(argument("variable", StringArgumentType.word())
                                            .executes((DebugCommand::onSetVariable))))));

    private static int onGetVariable(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.translatable(ModConstants.MESSAGE_COMMAND_NOT_PLAYER).withStyle(ChatFormatting.RED));
            return 0;
        }

        var name = StringArgumentType.getString(context, "name");

        var holder = InkraftPlatform.getPlayerData(player);
        var variable = holder.getVariable(name);

        player.sendSystemMessage(Component.literal("Variable " + name + ": " + variable.asString()));

        return 1;
    }

    private static int onSetVariable(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.translatable(ModConstants.MESSAGE_COMMAND_NOT_PLAYER).withStyle(ChatFormatting.RED));
            return 0;
        }

        var name = StringArgumentType.getString(context, "name");
        var variable = StringArgumentType.getString(context, "variable");

        var holder = InkraftPlatform.getPlayerData(player);
        holder.putVariable(name, "", false, IStoryVariable.IStoryVariable.fromString(variable));

        player.sendSystemMessage(Component.translatable(ModConstants.MESSAGE_COMMAND_SUCCESS)
                .withStyle(ChatFormatting.LIGHT_PURPLE));

        return 1;
    }
}
