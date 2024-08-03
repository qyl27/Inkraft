package cx.rain.mc.inkraft.neoforge;

import cx.rain.mc.inkraft.platform.IPermissionHolder;
import cx.rain.mc.inkraft.neoforge.platform.PermissionHolder;
import cx.rain.mc.inkraft.platform.IInkPlayerData;
import net.minecraft.world.entity.player.Player;

public class InkraftPlatformImpl {
    private static final IPermissionHolder PERMISSION_MANAGER = new PermissionHolder();

    public static IInkPlayerData getPlayerStoryStateHolder(Player player) {
//        return player.getCapability(InkraftCapabilities.INKRAFT_STORY_STATE_HOLDER);
        return null;
    }

    public static IPermissionHolder getPermissionManager() {
        return PERMISSION_MANAGER;
    }
}
