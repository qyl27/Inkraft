package cx.rain.mc.inkraft.forge;

import cx.rain.mc.inkraft.command.IInkPermissionManager;
import cx.rain.mc.inkraft.forge.capability.InkraftCapabilities;
import cx.rain.mc.inkraft.forge.platform.InkPermissionManagerForge;
import cx.rain.mc.inkraft.story.state.IInkStoryStateHolder;
import net.minecraft.world.entity.player.Player;

public class InkraftPlatformImpl {
    private static final IInkPermissionManager PERMISSION_MANAGER = new InkPermissionManagerForge();

    public static IInkStoryStateHolder getPlayerStoryStateHolder(Player player) {
        var cap = player.getCapability(InkraftCapabilities.INKRAFT_STORY_STATE_HOLDER);
        return cap.orElseThrow(RuntimeException::new);
    }

    public static IInkPermissionManager getPermissionManager() {
        return PERMISSION_MANAGER;
    }
}
