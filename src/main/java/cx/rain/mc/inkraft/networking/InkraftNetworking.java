package cx.rain.mc.inkraft.networking;

import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class InkraftNetworking {
    public InkraftNetworking() {
        // Todo: qyl27: Optional channel?
//        channel.register(S2CShowVariablePacket.class, S2CShowVariablePacket::encode, S2CShowVariablePacket::new, S2CShowVariablePacket::apply);
//        channel.register(S2CHideAllVariablePacket.class, S2CHideAllVariablePacket::encode, S2CHideAllVariablePacket::new, S2CHideAllVariablePacket::apply);
    }

    public void sendToPlayer(ServerPlayer player, Object msg) {
//        channel.sendToPlayer(player, msg);
    }

    public void sendToPlayers(List<ServerPlayer> player, Object msg) {
//        channel.sendToPlayers(player, msg);
    }

    public void sendToServer(Object msg) {
//        channel.sendToServer(msg);
    }
}
