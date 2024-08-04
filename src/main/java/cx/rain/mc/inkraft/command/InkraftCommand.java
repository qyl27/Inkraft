package cx.rain.mc.inkraft.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import cx.rain.mc.inkraft.ModConstants;
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
    public static final String ARGUMENT_ID = "id";
    public static final String ARGUMENT_PLAYER = "player";
    public static final String ARGUMENT_TOKEN = "token";
    public static final String ARGUMENT_CHOICE = "choice";

    public static final LiteralArgumentBuilder<CommandSourceStack> INKRAFT = literal("inkraft")
            .then(literal("version")
                    .executes(InkraftCommand::onVersion))
            .then(literal("start")
                    .requires(InkraftPlatform.getPermissionManager()::couldUse)
                    .then(argument(ARGUMENT_ID, ResourceLocationArgument.id())
                            .suggests(InkraftCommand::suggestStart)
                            .then(argument(ARGUMENT_PLAYER, EntityArgument.player())
                                    .requires(InkraftPlatform.getPermissionManager()::isAdmin)
                                    .executes(InkraftCommand::onStart))
                            .executes(InkraftCommand::onStart)))
            .then(literal("current")
                    .requires(InkraftPlatform.getPermissionManager()::couldUse)
                    .then(argument(ARGUMENT_PLAYER, EntityArgument.player())
                            .requires(InkraftPlatform.getPermissionManager()::isAdmin)
                            .executes(InkraftCommand::onCurrent))
                    .executes(InkraftCommand::onCurrent))
            .then(literal("next")
                    .requires(InkraftPlatform.getPermissionManager()::couldUse)
                    .then(argument(ARGUMENT_TOKEN, UuidArgument.uuid())
                            .then(argument(ARGUMENT_CHOICE, IntegerArgumentType.integer())
                                    .suggests(InkraftCommand::suggestChoice)
                                    .executes(InkraftCommand::onNextChoose))
                            .executes(InkraftCommand::onNext))
                    .then(argument(ARGUMENT_PLAYER, EntityArgument.player())
                            .requires(InkraftPlatform.getPermissionManager()::isAdmin)
                            .then(argument(ARGUMENT_CHOICE, IntegerArgumentType.integer())
                                    .suggests(InkraftCommand::suggestChoice)
                                    .executes(InkraftCommand::onNextChoose))
                            .executes(InkraftCommand::onNext)))
            .then(literal("reset")
                    .requires(InkraftPlatform.getPermissionManager()::isAdmin)
                    .then(argument(ARGUMENT_PLAYER, EntityArgument.player())
                            .executes(InkraftCommand::onReset))
                    .executes(InkraftCommand::onReset))
            .then(DebugCommand.INKRAFT_DEBUG);

    private static int onVersion(final CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() ->
                Component.literal("Inkraft ver: " + Inkraft.VERSION).withStyle(ChatFormatting.AQUA), true);
        return 1;
    }

    /// <editor-fold desc="Handle.">

    private static int onStart(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var id = ResourceLocationArgument.getId(context, ARGUMENT_ID);

        try {
            var object = EntityArgument.getPlayer(context, ARGUMENT_PLAYER);
            doStart(object, id);
            context.getSource().sendSuccess(() -> Component.literal("Success"), true);  // Todo.
            return 1;
        } catch (CommandSyntaxException ex) {
            throw ex;
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        doStart(player, id);
        return 1;
    }

    private static int onNext(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();

        try {
            var object = EntityArgument.getPlayer(context, ARGUMENT_PLAYER);
            doNext(object);
            context.getSource().sendSuccess(() -> Component.literal("Success"), true);  // Todo.
            return 1;
        } catch (CommandSyntaxException ex) {
            throw ex;
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        doNext(player);
        return 1;
    }

    private static int onNextChoose(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var choice = IntegerArgumentType.getInteger(context, ARGUMENT_CHOICE);

        try {
            var object = EntityArgument.getPlayer(context, ARGUMENT_PLAYER);
            doChoice(object, choice);
            context.getSource().sendSuccess(() -> Component.literal("Success"), true);  // Todo.
            return 1;
        } catch (CommandSyntaxException ex) {
            throw ex;
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        doChoice(player, choice);
        return 1;
    }

    private static int onCurrent(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();

        try {
            var object = EntityArgument.getPlayer(context, ARGUMENT_PLAYER);
            doCurrent(object);
            context.getSource().sendSuccess(() -> Component.literal("Success"), true);  // Todo.
            return 1;
        } catch (CommandSyntaxException ex) {
            throw ex;
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        doCurrent(player);
        return 1;
    }

    private static int onReset(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();

        try {
            var object = EntityArgument.getPlayer(context, ARGUMENT_PLAYER);
            doReset(object);
            context.getSource().sendSuccess(() -> Component.literal("Success"), true);  // Todo.
            return 1;
        } catch (CommandSyntaxException ex) {
            throw ex;
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        doReset(player);
        return 1;
    }

    /// </editor-fold>

    /// <editor-fold desc="Logic.">

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
        story.stop();
        story.getData().clearData();
    }

    /// </editor-fold>

    /// <editor-fold desc="Suggestions.">

    private static CompletableFuture<Suggestions> suggestStart(final CommandContext<CommandSourceStack> context,
                                                               final SuggestionsBuilder builder) throws CommandSyntaxException {
        for (var story : Inkraft.getInstance().getStoryRegistry().getAll()) {
            builder.suggest(story.toString());
        }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestChoice(final CommandContext<CommandSourceStack> context,
                                                               final SuggestionsBuilder builder) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        for (var choice : Inkraft.getInstance().getStoriesManager().get(player).getChoices()) {
            builder.suggest(choice.getIndex(), choice::getText);
        }
        return builder.buildFuture();
    }

    /// </editor-fold>
}
