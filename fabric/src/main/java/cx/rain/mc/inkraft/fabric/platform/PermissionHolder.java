package cx.rain.mc.inkraft.fabric.platform;

import cx.rain.mc.inkraft.platform.IPermissionHolder;
import net.minecraft.commands.CommandSourceStack;

public class PermissionHolder implements IPermissionHolder {

    @Override
    public boolean couldUse(CommandSourceStack source) {
        return source.hasPermission(0);
    }

    @Override
    public boolean isAdmin(CommandSourceStack source) {
        return source.hasPermission(2);
    }
}
