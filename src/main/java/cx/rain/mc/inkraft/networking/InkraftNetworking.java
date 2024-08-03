package cx.rain.mc.inkraft.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class InkraftNetworking {
    public static void register() {

    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload msg) {
        NetworkManager.sendToPlayer(player, msg);
    }

    public static void sendToPlayers(List<ServerPlayer> players, CustomPacketPayload msg) {
        players.forEach(p -> sendToPlayer(p, msg));
    }
}
