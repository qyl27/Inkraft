package cx.rain.mc.inkraft.platform;

import net.minecraft.commands.CommandSourceStack;

public interface IPermissionHolder {
    boolean couldUse(CommandSourceStack source);

    boolean isAdmin(CommandSourceStack source);
}
