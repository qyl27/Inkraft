package cx.rain.mc.inkraft.mod.utility;

import cx.rain.mc.inkraft.mod.InkraftMod;
import cx.rain.mc.inkraft.mod.networking.packet.S2CShowVariablePacket;
import net.minecraft.server.level.ServerPlayer;

public class ShowVariableHelper {
    public static void showVariable(ServerPlayer player, String name, String displayName, boolean isShow, String value) {
        InkraftMod.getInstance().getNetworking().sendToPlayer(player, new S2CShowVariablePacket(name, displayName, isShow, value));
    }
}
