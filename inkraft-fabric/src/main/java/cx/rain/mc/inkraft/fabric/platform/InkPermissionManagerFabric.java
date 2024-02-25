package cx.rain.mc.inkraft.fabric.platform;

import cx.rain.mc.inkraft.command.IInkPermissionManager;
import net.minecraft.commands.CommandSourceStack;

public class InkPermissionManagerFabric implements IInkPermissionManager {

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

    @Override
    public boolean hasStartForOtherPermission(CommandSourceStack source) {
        return source.hasPermission(2);
    }

    @Override
    public boolean hasContinueForOtherPermission(CommandSourceStack source) {
        return source.hasPermission(2);
    }
}
