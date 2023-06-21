package cx.rain.mc.inkraft.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import cx.rain.mc.inkraft.Inkraft;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class InkraftCommand {
    public static final LiteralArgumentBuilder<CommandSourceStack> INKRAFT = literal("inkraft")
            .then(literal("version")
                    .executes(InkraftCommand::onVersion))
            .then(literal("start")
                    .requires(Inkraft.getInstance().getPlatform().getPermissionManager()::hasStartPermission)
                    .then(argument("path", ResourceLocationArgument.id())
                            .suggests(InkraftCommand::suggestStart)
                            .executes(InkraftCommand::onStart)));

    private static int onVersion(final CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() ->
                Component.literal("Inkraft ver: " + Inkraft.VERSION).withStyle(ChatFormatting.AQUA), true);
        return 1;
    }

    private static int onStart(CommandContext<CommandSourceStack> context) {
        if (!ensurePlayer(context)) {
            return 0;
        }

        var player = context.getSource().getPlayer();
        var path = ResourceLocationArgument.getId(context, "path");

        var stateHolder = Inkraft.getInstance().getPlatform().getPlayerStoryStateHolder(player);
        var storiesManager = Inkraft.getInstance().getStoriesManager();

        var story = storiesManager.getStory(player, path);


        return 1;
    }

    private static CompletableFuture<Suggestions> suggestStart(final CommandContext<CommandSourceStack> context,
                                                               final SuggestionsBuilder builder) throws CommandSyntaxException {
        for (var story : Inkraft.getInstance().getStoriesManager().getStories()) {
            builder.suggest(story.toString());
        }
        return builder.buildFuture();
    }

    /// <editor-fold desc="Utility methods.">

    private static boolean ensurePlayer(final CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        if (source.getPlayer() != null) {
            return true;
        } else {
            source.sendFailure(Component.translatable(CommandConstants.MESSAGE_NOT_PLAYER).withStyle(ChatFormatting.RED));
            return false;
        }
    }

    /// </editor-fold>
}
