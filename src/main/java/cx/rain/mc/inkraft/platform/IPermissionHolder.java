package cx.rain.mc.inkraft.platform;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public interface IPermissionHolder {
    boolean canStart(CommandSourceStack source);

    boolean canNext(CommandSourceStack source);

    boolean canClear(CommandSourceStack source);

    boolean canStartFor(CommandSourceStack source, ServerPlayer player);

    boolean canNextFor(CommandSourceStack source, ServerPlayer player);

    boolean canClearFor(CommandSourceStack source, ServerPlayer player);
}
