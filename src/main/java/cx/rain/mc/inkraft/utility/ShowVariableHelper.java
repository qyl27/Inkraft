package cx.rain.mc.inkraft.utility;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.networking.packet.S2CShowVariablePacket;
import net.minecraft.server.level.ServerPlayer;

public class ShowVariableHelper {
    public static void showVariable(ServerPlayer player, String name, String displayName, boolean isShow, String value) {
        Inkraft.getInstance().getNetworking().sendToPlayer(player, new S2CShowVariablePacket(name, displayName, isShow, value));
    }
}
