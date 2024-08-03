package cx.rain.mc.inkraft.fabric;

import cx.rain.mc.inkraft.platform.IPermissionHolder;
import cx.rain.mc.inkraft.fabric.platform.PermissionHolder;
import cx.rain.mc.inkraft.fabric.platform.StoryStateHolder;
import cx.rain.mc.inkraft.platform.IInkPlayerData;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InkraftPlatformImpl {
    private static final Map<UUID, StoryStateHolder> CACHED_PLAYER_HOLDER = new HashMap<>();
    private static final IPermissionHolder PERMISSION_MANAGER = new PermissionHolder();

    public static IInkPlayerData getPlayerStoryStateHolder(Player player) {
        if (!CACHED_PLAYER_HOLDER.containsKey(player.getUUID())) {
            CACHED_PLAYER_HOLDER.put(player.getUUID(), new StoryStateHolder(player));
        }

        return CACHED_PLAYER_HOLDER.get(player.getUUID());
    }

    public static IPermissionHolder getPermissionManager() {
        return PERMISSION_MANAGER;
    }
}
