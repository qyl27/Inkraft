package cx.rain.mc.inkraft.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import cx.rain.mc.inkraft.Constants;
import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.platform.IInkPlayerData;
import cx.rain.mc.inkraft.story.StoryInstance;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class InkraftCommand {

    public static final LiteralArgumentBuilder<CommandSourceStack> INKRAFT = literal("inkraft")
            .then(literal("version")
                    .executes(InkraftCommand::onVersion))
            .then(literal("start")
                    .requires(InkraftPlatform.getPermissionManager()::hasStartPermission)
                    .then(argument("path", ResourceLocationArgument.id())
                            .suggests(InkraftCommand::suggestStart)
                            .then(argument("debug", BoolArgumentType.bool())
                                    .executes(InkraftCommand::onStartDebug))
                            .then(argument("player", EntityArgument.player())
                                    .then(argument("debug", BoolArgumentType.bool())
                                            .executes(InkraftCommand::onStartForOtherDebug))
                                    .executes(InkraftCommand::onStartForOther))
                            .executes(InkraftCommand::onStart)))
            .then(literal("continue")
                    .requires(InkraftPlatform.getPermissionManager()::hasContinuePermission)
                    .then(argument("token", UuidArgument.uuid())
                            .then(argument("choice", IntegerArgumentType.integer())
                                    .executes(InkraftCommand::onChoiceContinue))
                            .executes(InkraftCommand::onSimpleContinue))
                    .then(argument("player", EntityArgument.player())
                            .requires(InkraftPlatform.getPermissionManager()::hasContinueForOtherPermission)
                            .then(argument("choice", IntegerArgumentType.integer())
                                    .executes(InkraftCommand::onChoiceContinueForOther))
                            .executes(InkraftCommand::onSimpleContinueForOther)))
            .then(literal("clear")
                    .requires(InkraftPlatform.getPermissionManager()::hasClearPermission)
                    .then(argument("player", EntityArgument.player())
                            .executes(InkraftCommand::onClearStateForOther))
                    .executes(InkraftCommand::onClearState))
            .then(literal("repeat")
                    .requires(InkraftPlatform.getPermissionManager()::hasContinuePermission)
                    .then(argument("player", EntityArgument.player())
                            .executes(InkraftCommand::onRepeatForOther))
                    .executes(InkraftCommand::onRepeat))
            .then(DebugCommand.INKRAFT_DEBUG);

    private static int onVersion(final CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() ->
                Component.literal("Inkraft ver: " + Inkraft.VERSION).withStyle(ChatFormatting.AQUA), true);
        return 1;
    }

    /// <editor-fold desc="Start.">

    private static int onStart(final CommandContext<CommandSourceStack> context) {
        if (!ensurePlayer(context)) {
            return 0;
        }

        var player = context.getSource().getPlayer();
        var path = ResourceLocationArgument.getId(context, "path");
        var stateHolder = InkraftPlatform.getPlayerStoryStateHolder(player);

        startStory(player, path, stateHolder, false);

        return 1;
    }

    private static int onStartDebug(final CommandContext<CommandSourceStack> context) {
        if (!ensurePlayer(context)) {
            return 0;
        }

        var player = context.getSource().getPlayer();
        var path = ResourceLocationArgument.getId(context, "path");
        var debug = BoolArgumentType.getBool(context, "debug");
        var stateHolder = InkraftPlatform.getPlayerStoryStateHolder(player);

        startStory(player, path, stateHolder, debug);

        return 1;
    }

    private static int onStartForOther(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = EntityArgument.getPlayer(context, "player");
        var path = ResourceLocationArgument.getId(context, "path");
        var stateHolder = InkraftPlatform.getPlayerStoryStateHolder(player);

        startStory(player, path, stateHolder, false);

        return 1;
    }

    private static int onStartForOtherDebug(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = EntityArgument.getPlayer(context, "player");
        var path = ResourceLocationArgument.getId(context, "path");
        var debug = BoolArgumentType.getBool(context, "debug");
        var stateHolder = InkraftPlatform.getPlayerStoryStateHolder(player);

        startStory(player, path, stateHolder, debug);
        return 1;
    }

    /// </editor-fold>

    /// <editor-fold desc="Continue.">

    private static int onSimpleContinue(final CommandContext<CommandSourceStack> context) {
        if (!ensurePlayer(context)) {
            return 0;
        }

        var player = context.getSource().getPlayer();
        var token = UuidArgument.getUuid(context, "token");
        var stateHolder = InkraftPlatform.getPlayerStoryStateHolder(player);

        if (stateHolder.getContinueToken() != null && stateHolder.getContinueToken().equals(token)) {
            continueStory(player, stateHolder, -1);
        } else {
            player.sendSystemMessage(Component.translatable(Constants.MESSAGE_STORY_BAD_TOKEN).withStyle(ChatFormatting.RED));

            return 1;
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
        var stateHolder = InkraftPlatform.getPlayerStoryStateHolder(player);

        if (stateHolder.getContinueToken() != null && stateHolder.getContinueToken().equals(token)) {
            continueStory(player, stateHolder, choice);
        } else {
            player.sendSystemMessage(Component.translatable(Constants.MESSAGE_STORY_BAD_TOKEN).withStyle(ChatFormatting.RED));

            return 1;
        }

        return 1;
    }

    private static int onSimpleContinueForOther(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = EntityArgument.getPlayer(context, "player");
        var stateHolder = InkraftPlatform.getPlayerStoryStateHolder(player);

        continueStory(player, stateHolder, -1);
        return 1;
    }

    private static int onChoiceContinueForOther(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = EntityArgument.getPlayer(context, "player");
        var stateHolder = InkraftPlatform.getPlayerStoryStateHolder(player);
        var choice = IntegerArgumentType.getInteger(context, "choice");

        continueStory(player, stateHolder, choice);
        return 1;
    }

    /// </editor-fold>

    /// <editor-fold desc="Clear states.">

    private static int onClearState(CommandContext<CommandSourceStack> context) {
        if (!ensurePlayer(context)) {
            return 0;
        }

        var player = context.getSource().getPlayer();
        var stateHolder = InkraftPlatform.getPlayerStoryStateHolder(player);

        stateHolder.clearData();
        Inkraft.getInstance().getStoryRegistry().refreshStory(player);

        player.sendSystemMessage(Component.translatable(Constants.MESSAGE_COMMAND_SUCCESS)
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        return 1;
    }

    private static int onClearStateForOther(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = EntityArgument.getPlayer(context, "player");
        var stateHolder = InkraftPlatform.getPlayerStoryStateHolder(player);

        stateHolder.clearData();
        Inkraft.getInstance().getStoryRegistry().refreshStory(player);

        context.getSource().sendSystemMessage(Component.translatable(Constants.MESSAGE_COMMAND_SUCCESS)
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        return 1;
    }

    /// </editor-fold>

    /// <editor-fold desc="Repeat.">

    private static int onRepeat(CommandContext<CommandSourceStack> context) {
        if (!ensurePlayer(context)) {
            return 0;
        }

        var player = context.getSource().getPlayer();

        repeatChoices(player);

        return 1;
    }

    private static int onRepeatForOther(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = EntityArgument.getPlayer(context, "player");

        repeatChoices(player);

        return 1;
    }

    /// </editor-fold>

    /// <editor-fold desc="Utility methods.">

    private static void startStory(ServerPlayer player, ResourceLocation path, IInkPlayerData stateHolder, boolean isDebug) {
        var storiesManager = Inkraft.getInstance().getStoryRegistry();

        StoryInstance story;
        if (!storiesManager.hasCachedStory(player)) {
            story = storiesManager.createStory(player);
        } else {
            story = storiesManager.getStory(player);
        }

        story.startStory(path, isDebug);
    }

    private static void continueStory(ServerPlayer player, IInkPlayerData holder, int choice) {
        var storiesManager = Inkraft.getInstance().getStoryRegistry();
        var story = storiesManager.getStory(player);

        if (holder.isInStory()) {
            if (choice == -1) {
                story.continueStoryWithoutChoice();
            } else {
                story.continueStoryWithChoice(choice);
            }
        }
    }

    private static void repeatChoices(ServerPlayer player) {
        var storiesManager = Inkraft.getInstance().getStoryRegistry();

        StoryInstance story;
        if (!storiesManager.hasCachedStory(player)) {
            story = storiesManager.createStory(player);
        } else {
            story = storiesManager.getStory(player);
        }

        var holder = InkraftPlatform.getPlayerStoryStateHolder(player);
        story.load(holder.getCurrentStory());
        story.repeatContinue();
    }

    private static CompletableFuture<Suggestions> suggestStart(final CommandContext<CommandSourceStack> context,
                                                               final SuggestionsBuilder builder) throws CommandSyntaxException {
        for (var story : Inkraft.getInstance().getStoryRegistry().getStories()) {
            builder.suggest(story.toString());
        }
        return builder.buildFuture();
    }

    private static boolean ensurePlayer(final CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        if (source.getPlayer() != null) {
            return true;
        } else {
            source.sendFailure(Component.translatable(Constants.MESSAGE_COMMAND_NOT_PLAYER).withStyle(ChatFormatting.RED));
            return false;
        }
    }

    /// </editor-fold>
}
