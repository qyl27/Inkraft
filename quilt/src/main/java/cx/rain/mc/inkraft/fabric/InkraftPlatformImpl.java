package cx.rain.mc.inkraft.fabric;

import cx.rain.mc.inkraft.command.IInkPermissionManager;
import cx.rain.mc.inkraft.quilt.platform.InkPermissionManagerQuilt;
import cx.rain.mc.inkraft.quilt.platform.InkStoryStateHolderQuilt;
import cx.rain.mc.inkraft.story.state.IInkStoryStateHolder;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InkraftPlatformImpl {
    private static final Map<UUID, InkStoryStateHolderQuilt> CACHED_PLAYER_HOLDER = new HashMap<>();
    private static final IInkPermissionManager PERMISSION_MANAGER = new InkPermissionManagerQuilt();

    public static IInkStoryStateHolder getPlayerStoryStateHolder(ServerPlayer player) {
        if (!CACHED_PLAYER_HOLDER.containsKey(player.getUUID())) {
            CACHED_PLAYER_HOLDER.put(player.getUUID(), new InkStoryStateHolderQuilt(player));
        }

        return CACHED_PLAYER_HOLDER.get(player.getUUID());
    }

    public static IInkPermissionManager getPermissionManager() {
        return PERMISSION_MANAGER;
    }
}
