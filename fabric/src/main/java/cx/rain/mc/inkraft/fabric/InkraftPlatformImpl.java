package cx.rain.mc.inkraft.fabric;

import cx.rain.mc.inkraft.fabric.data.InkraftData;
import cx.rain.mc.inkraft.platform.IPermissionHolder;
import cx.rain.mc.inkraft.fabric.platform.PermissionHolder;
import cx.rain.mc.inkraft.platform.IInkPlayerData;
import net.minecraft.world.entity.player.Player;

public class InkraftPlatformImpl {
    private static final IPermissionHolder PERMISSION_MANAGER = new PermissionHolder();

    public static IInkPlayerData getPlayerData(Player player) {
        return InkraftData.get(player.getServer()).get(player);
    }

    public static IPermissionHolder getPermissionManager() {
        return PERMISSION_MANAGER;
    }
}
