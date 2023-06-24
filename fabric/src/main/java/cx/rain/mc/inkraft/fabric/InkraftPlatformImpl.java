package cx.rain.mc.inkraft.fabric;

import cx.rain.mc.inkraft.command.IInkPermissionManager;
import cx.rain.mc.inkraft.fabric.platform.InkPermissionManagerFabric;
import cx.rain.mc.inkraft.fabric.platform.InkStoryStateHolderFabric;
import cx.rain.mc.inkraft.story.IInkStoryStateHolder;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InkraftPlatformImpl {
    private static final Map<UUID, InkStoryStateHolderFabric> CACHED_PLAYER_HOLDER = new HashMap<>();
    private static final IInkPermissionManager PERMISSION_MANAGER = new InkPermissionManagerFabric();

    public static IInkStoryStateHolder getPlayerStoryStateHolder(ServerPlayer player) {
        if (!CACHED_PLAYER_HOLDER.containsKey(player.getUUID())) {
            CACHED_PLAYER_HOLDER.put(player.getUUID(), new InkStoryStateHolderFabric(player));
        }

        return CACHED_PLAYER_HOLDER.get(player.getUUID());
    }

    public static IInkPermissionManager getPermissionManager() {
        return PERMISSION_MANAGER;
    }
}
