package cx.rain.mc.inkraft.utility;

import cx.rain.mc.inkraft.Inkraft;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class InkTagHelper {
    private static Logger LOGGER = LoggerFactory.getLogger(Inkraft.MODID);

    public static List<TagOperation> parseTag(List<String> tags) {
        var operations = new ArrayList<TagOperation>();

        var it = tags.iterator();
        while (it.hasNext()) {
            var op = it.next();
            var operator = switch (op) {
                case "COMMAND" -> TagOperator.COMMAND;
                case "GIVE" -> TagOperator.GIVE;
                default -> TagOperator.UNKNOWN;
            };

            var args = new ArrayList<String>();
            for (var i = 0; i < operator.getArgumentsCount(); i++) {
                args.add(it.next());
            }

            if (operator != TagOperator.UNKNOWN) {
                operations.add(new TagOperation(operator, args));
            }
        }

        return operations;
    }

    public static void runTagCommands(List<TagOperation> operations, MinecraftServer server, ServerPlayer player) {
        for (var op : operations) {
            if (op.operator() == TagOperator.COMMAND) {
                for (var command : op.args()) {
                    var result = server.getCommands().performPrefixedCommand(player.createCommandSourceStack(), command) != 0;
                    if (!result) {
                        LOGGER.error("Command run failed: " + command);
                    }
                }
            } else if (op.operator() == TagOperator.GIVE) {
                for (var item : op.args()) {
                    try {
                        var itemStack = ItemStack.of(TagParser.parseTag(item));
                        var result = giveItem(player, itemStack);

                        if (!result) {
                            LOGGER.error("Give item failed: " + item);
                        }
                    } catch (Exception ex) {
                        LOGGER.error("Give item failed: " + item);
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private static boolean giveItem(ServerPlayer player, ItemStack stack) {
        var result = player.getInventory().add(stack);
        if (!result || !stack.isEmpty()) {
            var itemEntity = player.drop(stack, false);
            if (itemEntity != null) {
                itemEntity.setNoPickUpDelay();
                itemEntity.setTarget(player.getUUID());
            }
        }
        return true;
    }

    public record TagOperation(TagOperator operator, List<String> args) {
    }

    public enum TagOperator {
        UNKNOWN(0),
        COMMAND(1),
        GIVE(1),
        ;

        private final int argc;

        TagOperator(int argc) {
            this.argc = argc;
        }

        public int getArgumentsCount() {
            return argc;
        }
    }
}
