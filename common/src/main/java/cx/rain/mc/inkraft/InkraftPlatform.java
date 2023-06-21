package cx.rain.mc.inkraft;

import cx.rain.mc.inkraft.command.IInkPermissionManager;
import cx.rain.mc.inkraft.story.IInkStoryStateHolder;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.level.ServerPlayer;

public class InkraftPlatform {
    @ExpectPlatform
    public IInkStoryStateHolder getPlayerStoryStateHolder(ServerPlayer player) {
        throw new RuntimeException();
    }

    @ExpectPlatform
    public IInkPermissionManager getPermissionManager() {
        throw new RuntimeException();
    }
}
