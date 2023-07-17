package cx.rain.mc.inkraft;

import cx.rain.mc.inkraft.command.IInkPermissionManager;
import cx.rain.mc.inkraft.story.state.IInkStoryStateHolder;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.player.Player;

public class InkraftPlatform {
    @ExpectPlatform
    public static IInkStoryStateHolder getPlayerStoryStateHolder(Player player) {
        throw new RuntimeException();
    }

    @ExpectPlatform
    public static IInkPermissionManager getPermissionManager() {
        throw new RuntimeException();
    }
}
