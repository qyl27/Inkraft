package cx.rain.mc.inkraft.command;

import net.minecraft.commands.CommandSourceStack;

public interface IInkPermissionManager {
    boolean hasStartPermission(CommandSourceStack source);

    boolean hasContinuePermission(CommandSourceStack source);

    boolean hasClearPermission(CommandSourceStack source);

    boolean hasStartForOtherPermission(CommandSourceStack source);

    boolean hasContinueForOtherPermission(CommandSourceStack source);
}
