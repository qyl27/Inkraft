package cx.rain.mc.inkraft;

import cx.rain.mc.inkraft.command.IInkPermissionManager;
import cx.rain.mc.inkraft.story.IInkStoryStateHolder;
import cx.rain.mc.inkraft.timer.IInkTimerManager;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.level.ServerPlayer;

public class InkraftPlatform {
    @ExpectPlatform
    public static IInkStoryStateHolder getPlayerStoryStateHolder(ServerPlayer player) {
        throw new RuntimeException();
    }

    @ExpectPlatform
    public static IInkPermissionManager getPermissionManager() {
        throw new RuntimeException();
    }
}
