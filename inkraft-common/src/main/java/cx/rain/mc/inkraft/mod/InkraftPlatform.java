package cx.rain.mc.inkraft.mod;

import cx.rain.mc.inkraft.mod.platform.IPermissionHolder;
import cx.rain.mc.inkraft.mod.platform.IStoryHolder;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.player.Player;

public class InkraftPlatform {
    @ExpectPlatform
    public static IStoryHolder getPlayerStoryStateHolder(Player player) {
        throw new RuntimeException();
    }

    @ExpectPlatform
    public static IPermissionHolder getPermissionManager() {
        throw new RuntimeException();
    }
}
