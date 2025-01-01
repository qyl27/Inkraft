package cx.rain.mc.inkraft.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.CompletableFuture;

import static cx.rain.mc.inkraft.command.VariablesCommand.INKRAFT_VARIABLES;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class InkraftCommand {
    public static final String ARGUMENT_ID = "id";
    public static final String ARGUMENT_PLAYER = "player";
    public static final String ARGUMENT_TOKEN = "token";
    public static final String ARGUMENT_CHOICE = "optionIndex";

    public static final LiteralArgumentBuilder<CommandSourceStack> INKRAFT;

    static {
        INKRAFT = literal("inkraft")
                .then(INKRAFT_VARIABLES)
                .then(literal("version")
                        .executes(InkraftCommand::onVersion));

        INKRAFT.then(literal("start")
                .requires(InkraftPlatform.getPermissionManager()::couldUse)
                .then(argument(ARGUMENT_ID, ResourceLocationArgument.id())
                        .suggests(InkraftCommand::suggestStart)
                        .then(argument(ARGUMENT_PLAYER, EntityArgument.player())
                                .requires(InkraftPlatform.getPermissionManager()::isAdmin)
                                .executes(InkraftCommand::onStartPlayer))
                        .executes(InkraftCommand::onStart)))
                .then(literal("next")
                        .requires(InkraftPlatform.getPermissionManager()::couldUse)
                        .then(argument(ARGUMENT_TOKEN, UuidArgument.uuid())
                                .then(argument(ARGUMENT_CHOICE, IntegerArgumentType.integer())
                                        // qyl27: Players should never use it.
                                    .suggests(InkraftCommand::suggestChoice)
                                    .executes(InkraftCommand::onNextChoose))
                                .executes(InkraftCommand::onNext))
                        .then(argument(ARGUMENT_PLAYER, EntityArgument.player())
                                .requires(InkraftPlatform.getPermissionManager()::isAdmin)
                                .then(argument(ARGUMENT_CHOICE, IntegerArgumentType.integer())
                                        .suggests(InkraftCommand::suggestChoice)
                                        .executes(InkraftCommand::onNextChoosePlayer))
                                .executes(InkraftCommand::onNextPlayer)));

        INKRAFT.then(literal("current")
                .requires(InkraftPlatform.getPermissionManager()::couldUse)
                .executes(InkraftCommand::onCurrent)
                .then(argument(ARGUMENT_PLAYER, EntityArgument.player())
                        .requires(InkraftPlatform.getPermissionManager()::isAdmin)
                        .executes(InkraftCommand::onCurrentPlayer)));

        INKRAFT.then(literal("reset")
                .requires(InkraftPlatform.getPermissionManager()::isAdmin)
                .executes(InkraftCommand::onReset)
                .then(argument(ARGUMENT_PLAYER, EntityArgument.player())
                        .requires(InkraftPlatform.getPermissionManager()::isAdmin)
                        .executes(InkraftCommand::onResetPlayer)));
    }

    // <editor-fold desc="Handle.">

    private static int onVersion(final CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.translatable(ModConstants.Messages.COMMAND_VERSION, Inkraft.VERSION, Inkraft.BUILD_TIME.toString()).withStyle(ChatFormatting.LIGHT_PURPLE), true);
        return 1;
    }

    private static int onStart(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var id = ResourceLocationArgument.getId(context, ARGUMENT_ID);
        var player = context.getSource().getPlayerOrException();
        doStart(player, id);
        return 1;
    }

    private static int onStartPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var id = ResourceLocationArgument.getId(context, ARGUMENT_ID);
        var player = EntityArgument.getPlayer(context, ARGUMENT_PLAYER);
        doStart(player, id);
        return 1;
    }

    private static int onNext(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var token = UuidArgument.getUuid(context, ARGUMENT_TOKEN);
        var player = context.getSource().getPlayerOrException();

        var data = InkraftPlatform.getPlayerData(player);
        if (data.getContinuousToken() == null || !data.getContinuousToken().equals(token)) {
            context.getSource().sendFailure(Component.translatable(ModConstants.Messages.STORY_OPTION_OUTDATED).withStyle(ChatFormatting.RED));
            return 0;
        }

        doNext(player);
        return 1;
    }

    private static int onNextChoose(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var token = UuidArgument.getUuid(context, ARGUMENT_TOKEN);
        var player = context.getSource().getPlayerOrException();
        var choice = IntegerArgumentType.getInteger(context, ARGUMENT_CHOICE);

        var data = InkraftPlatform.getPlayerData(player);
        if (data.getContinuousToken() == null || !data.getContinuousToken().equals(token)) {
            context.getSource().sendFailure(Component.translatable(ModConstants.Messages.STORY_OPTION_OUTDATED).withStyle(ChatFormatting.RED));
            return 0;
        }

        doChoice(player, choice);
        return 1;
    }

    private static int onNextPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = EntityArgument.getPlayer(context, ARGUMENT_PLAYER);

        var data = InkraftPlatform.getPlayerData(player);
        if (data.getContinuousToken() == null) {
            context.getSource().sendFailure(Component.translatable(ModConstants.Messages.STORY_OPTION_OUTDATED).withStyle(ChatFormatting.RED));
            return 0;
        }

        doNext(player);
        return 1;
    }

    private static int onNextChoosePlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = EntityArgument.getPlayer(context, ARGUMENT_PLAYER);
        var choice = IntegerArgumentType.getInteger(context, ARGUMENT_CHOICE);

        var data = InkraftPlatform.getPlayerData(player);
        if (data.getContinuousToken() == null) {
            context.getSource().sendFailure(Component.translatable(ModConstants.Messages.STORY_OPTION_OUTDATED).withStyle(ChatFormatting.RED));
            return 0;
        }

        doChoice(player, choice);
        return 1;
    }

    private static int onCurrent(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var entity = context.getSource().getEntity();
        if (!(entity instanceof ServerPlayer)) {
            entity = context.getSource().getPlayerOrException();
        }
        var player = (ServerPlayer) entity;

        doCurrent(player);
        return 1;
    }

    private static int onCurrentPlayer(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var entity = context.getSource().getEntity();
        var player = (ServerPlayer) entity;

        doCurrent(player);
        return 1;
    }

    private static int onReset(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();

        doReset(player);
        context.getSource().sendSuccess(() -> Component.translatable(ModConstants.Messages.COMMAND_SUCCESS).withStyle(ChatFormatting.LIGHT_PURPLE), true);
        return 1;
    }

    private static int onResetPlayer(final CommandContext<CommandSourceStack> context) {
        var entity = context.getSource().getEntity();
        var player = (ServerPlayer) entity;

        doReset(player);
        context.getSource().sendSuccess(() -> Component.translatable(ModConstants.Messages.COMMAND_SUCCESS).withStyle(ChatFormatting.LIGHT_PURPLE), true);
        return 1;
    }

    // </editor-fold>

    // <editor-fold desc="Logic.">

    private static void doStart(ServerPlayer player, ResourceLocation id) {
        var story = Inkraft.getInstance().getStoriesManager().get(player);
        story.stop();
        story.newStory(id);
        story.start();
    }

    private static void doNext(ServerPlayer player) {
        var story = Inkraft.getInstance().getStoriesManager().get(player);
        story.stop();
        story.nextLine();
        story.start();
    }

    private static void doChoice(ServerPlayer player, int index) {
        var story = Inkraft.getInstance().getStoriesManager().get(player);
        story.stop();
        story.choose(index);
        story.start();
    }

    private static void doCurrent(ServerPlayer player) {
        var story = Inkraft.getInstance().getStoriesManager().get(player);
        story.stop();
        story.loadStory();
        story.start();
    }

    private static void doReset(ServerPlayer player) {
        var story = Inkraft.getInstance().getStoriesManager().get(player);
        story.getData().clearData();
        story.stop();
    }

    // </editor-fold>

    // <editor-fold desc="Suggestions.">

    private static CompletableFuture<Suggestions> suggestStart(final CommandContext<CommandSourceStack> context,
                                                               final SuggestionsBuilder builder) throws CommandSyntaxException {
        for (var story : Inkraft.getInstance().getStoryRegistry().getAll()) {
            builder.suggest(story.toString());
        }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestChoice(final CommandContext<CommandSourceStack> context,
                                                                final SuggestionsBuilder builder) throws CommandSyntaxException {
        try {
            var object = EntityArgument.getPlayer(context, ARGUMENT_PLAYER);
            for (var choice : Inkraft.getInstance().getStoriesManager().get(object).getChoices()) {
                builder.suggest(choice.getIndex(), choice::getText);
            }
            return builder.buildFuture();
        } catch (IllegalArgumentException ignored) {
        }

        var player = context.getSource().getPlayerOrException();
        for (var choice : Inkraft.getInstance().getStoriesManager().get(player).getChoices()) {
            builder.suggest(choice.getIndex(), choice::getText);
        }
        return builder.buildFuture();
    }

    // </editor-fold>
}
