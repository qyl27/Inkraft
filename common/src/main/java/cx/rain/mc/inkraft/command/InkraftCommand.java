package cx.rain.mc.inkraft.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import cx.rain.mc.inkraft.Inkraft;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;

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
                            .executes(InkraftCommand::onStart)))
            .then(literal("continue")
                    .requires(Inkraft.getInstance().getPlatform().getPermissionManager()::hasContinuePermission)
                    .then(argument("token", UuidArgument.uuid())
                            .executes(InkraftCommand::onSimpleContinue)
                            .then(argument("choice", IntegerArgumentType.integer())
                                    .executes(InkraftCommand::onChoiceContinue))))
            .then(literal("clear")
                    .requires(Inkraft.getInstance().getPlatform().getPermissionManager()::hasClearPermission)
                    .executes(InkraftCommand::onClearState));

    private static int onClearState(CommandContext<CommandSourceStack> context) {
        if (!ensurePlayer(context)) {
            return 0;
        }

        var player = context.getSource().getPlayer();
        var stateHolder = Inkraft.getInstance().getPlatform().getPlayerStoryStateHolder(player);

        stateHolder.setState("");
        Inkraft.getInstance().getStoriesManager().refreshStory(player);

        player.sendSystemMessage(Component.translatable(CommandConstants.MESSAGE_COMMAND_SUCCESS)
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        return 1;
    }

    private static int onVersion(final CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() ->
                Component.literal("Inkraft ver: " + Inkraft.VERSION).withStyle(ChatFormatting.AQUA), true);
        return 1;
    }

    private static int onStart(final CommandContext<CommandSourceStack> context) {
        if (!ensurePlayer(context)) {
            return 0;
        }

        var player = context.getSource().getPlayer();
        var path = ResourceLocationArgument.getId(context, "path");

        var stateHolder = Inkraft.getInstance().getPlatform().getPlayerStoryStateHolder(player);

        var storiesManager = Inkraft.getInstance().getStoriesManager();
        var story = storiesManager.getOrCreateStory(player, path);

        story.startStory(player, stateHolder);

        return 1;
    }

    private static int onSimpleContinue(final CommandContext<CommandSourceStack> context) {
        if (!ensurePlayer(context)) {
            return 0;
        }

        var player = context.getSource().getPlayer();
        var token = UuidArgument.getUuid(context, "token");
        var stateHolder = Inkraft.getInstance().getPlatform().getPlayerStoryStateHolder(player);

        if (stateHolder.getContinueToken().equals(token)) {
            var storiesManager = Inkraft.getInstance().getStoriesManager();
            var story = storiesManager.getStory(player);

            story.continueStoryWithoutChoice(player, stateHolder);
        }

        return 1;
    }

    private static int onChoiceContinue(final CommandContext<CommandSourceStack> context) {
        if (!ensurePlayer(context)) {
            return 0;
        }
        var player = context.getSource().getPlayer();
        var token = UuidArgument.getUuid(context, "token");
        var choice = IntegerArgumentType.getInteger(context, "choice");
        var stateHolder = Inkraft.getInstance().getPlatform().getPlayerStoryStateHolder(player);

        if (stateHolder.getContinueToken().equals(token)) {
            var storiesManager = Inkraft.getInstance().getStoriesManager();
            var story = storiesManager.getStory(player);

            story.continueStoryWithChoice(player, stateHolder, choice);
        } else {
            player.sendSystemMessage(Component.translatable(CommandConstants.MESSAGE_STORY_BAD_TOKEN).withStyle(ChatFormatting.RED));

            return 1;
        }

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
            source.sendFailure(Component.translatable(CommandConstants.MESSAGE_COMMAND_NOT_PLAYER).withStyle(ChatFormatting.RED));
            return false;
        }
    }

    /// </editor-fold>
}
