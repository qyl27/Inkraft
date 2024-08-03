package cx.rain.mc.inkraft;

import cx.rain.mc.inkraft.platform.IPermissionHolder;
import cx.rain.mc.inkraft.platform.IInkPlayerData;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.player.Player;

public class InkraftPlatform {
    @ExpectPlatform
    public static IInkPlayerData getPlayerData(Player player) {
        throw new RuntimeException();
    }

    @ExpectPlatform
    public static IPermissionHolder getPermissionManager() {
        throw new RuntimeException();
    }
}
