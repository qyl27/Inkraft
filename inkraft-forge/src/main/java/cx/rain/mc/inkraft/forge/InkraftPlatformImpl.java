package cx.rain.mc.inkraft.forge;

import cx.rain.mc.inkraft.mod.platform.IPermissionHolder;
import cx.rain.mc.inkraft.forge.capability.InkraftCapabilities;
import cx.rain.mc.inkraft.forge.platform.PermissionHolder;
import cx.rain.mc.inkraft.mod.platform.IStoryHolder;
import net.minecraft.world.entity.player.Player;

public class InkraftPlatformImpl {
    private static final IPermissionHolder PERMISSION_MANAGER = new PermissionHolder();

    public static IStoryHolder getPlayerStoryStateHolder(Player player) {
        var cap = player.getCapability(InkraftCapabilities.INKRAFT_STORY_STATE_HOLDER);
        return cap.orElseThrow(RuntimeException::new);
    }

    public static IPermissionHolder getPermissionManager() {
        return PERMISSION_MANAGER;
    }
}
