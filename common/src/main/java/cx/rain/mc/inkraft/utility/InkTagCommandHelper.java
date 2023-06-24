package cx.rain.mc.inkraft.utility;

import cx.rain.mc.inkraft.story.command.StoryCommand;
import cx.rain.mc.inkraft.story.command.StoryCommands;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InkTagCommandHelper {
    private static final Map<String, StoryCommand> COMMANDS_CACHE = new HashMap<>();

    static {
        for (var commandSupplier : StoryCommands.COMMANDS) {
            var command = commandSupplier.get();

            var commandName = command.getName().isBlank() ? commandSupplier.getId().getPath() : command.getName();
            COMMANDS_CACHE.put(commandName, command);
        }
    }

    public static List<TagOperation> parseTag(List<String> tags) {
        var operations = new ArrayList<TagOperation>();

        var it = tags.iterator();
        while (it.hasNext()) {
            var op = it.next();

            if (!COMMANDS_CACHE.containsKey(op)) {
                continue;
            }

            var cmd = COMMANDS_CACHE.get(op);
            var args = new ArrayList<String>();
            for (var i = 0; i < cmd.getArgumentsCount(); i++) {
                args.add(it.next());
            }

            operations.add(new TagOperation(op, args));
        }

        return operations;
    }

    public static void runTagCommands(List<TagOperation> operations, ServerPlayer player) {
        for (var op : operations) {
            var command = COMMANDS_CACHE.get(op.operator());
            command.getConsumer().accept(op.args().toArray(new String[0]), player);
        }
    }

    public record TagOperation(String operator, List<String> args) {
    }
}
