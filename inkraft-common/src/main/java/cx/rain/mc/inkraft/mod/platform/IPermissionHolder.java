package cx.rain.mc.inkraft.mod.platform;

import net.minecraft.commands.CommandSourceStack;

public interface IPermissionHolder {
    boolean hasStartPermission(CommandSourceStack source);

    boolean hasContinuePermission(CommandSourceStack source);

    boolean hasClearPermission(CommandSourceStack source);

    boolean hasStartForOtherPermission(CommandSourceStack source);

    boolean hasContinueForOtherPermission(CommandSourceStack source);
}
