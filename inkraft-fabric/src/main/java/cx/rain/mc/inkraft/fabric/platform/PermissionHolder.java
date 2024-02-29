package cx.rain.mc.inkraft.fabric.platform;

import cx.rain.mc.inkraft.platform.IPermissionHolder;
import net.minecraft.commands.CommandSourceStack;

public class PermissionHolder implements IPermissionHolder {

    @Override
    public boolean canStart(CommandSourceStack source) {
        return source.hasPermission(2);
    }

    @Override
    public boolean canNext(CommandSourceStack source) {
        return source.hasPermission(0);
    }

    @Override
    public boolean canClear(CommandSourceStack source) {
        return source.hasPermission(2);
    }

    @Override
    public boolean canStartForOther(CommandSourceStack source) {
        return source.hasPermission(2);
    }

    @Override
    public boolean canNextForOther(CommandSourceStack source) {
        return source.hasPermission(2);
    }
}
