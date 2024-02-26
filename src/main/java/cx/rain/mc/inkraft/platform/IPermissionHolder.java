package cx.rain.mc.inkraft.platform;

import net.minecraft.commands.CommandSourceStack;

public interface IPermissionHolder {
    boolean hasStartPermission(CommandSourceStack source);

    boolean hasContinuePermission(CommandSourceStack source);

    boolean hasClearPermission(CommandSourceStack source);

    boolean hasStartForOtherPermission(CommandSourceStack source);

    boolean hasContinueForOtherPermission(CommandSourceStack source);
}
