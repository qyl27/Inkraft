package cx.rain.mc.inkraft.quilt.platform;

import cx.rain.mc.inkraft.command.IInkPermissionManager;
import net.minecraft.commands.CommandSourceStack;

public class InkPermissionManagerQuilt implements IInkPermissionManager {

    @Override
    public boolean hasStartPermission(CommandSourceStack source) {
        return source.hasPermission(2);
    }

    @Override
    public boolean hasContinuePermission(CommandSourceStack source) {
        return source.hasPermission(0);
    }

    @Override
    public boolean hasClearPermission(CommandSourceStack source) {
        return source.hasPermission(2);
    }
}
