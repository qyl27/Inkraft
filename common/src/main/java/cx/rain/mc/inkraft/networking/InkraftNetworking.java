package cx.rain.mc.inkraft.networking;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.networking.packet.S2CHideAllVariablePacket;
import cx.rain.mc.inkraft.networking.packet.S2CShowVariablePacket;
import dev.architectury.networking.NetworkChannel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class InkraftNetworking {
    private final NetworkChannel channel = NetworkChannel.create(new ResourceLocation(Inkraft.MODID, "channel_variables"));

    public InkraftNetworking() {
        channel.register(S2CShowVariablePacket.class, S2CShowVariablePacket::encode, S2CShowVariablePacket::new, S2CShowVariablePacket::apply);
        channel.register(S2CHideAllVariablePacket.class, S2CHideAllVariablePacket::encode, S2CHideAllVariablePacket::new, S2CHideAllVariablePacket::apply);
    }

    public void sendToPlayer(ServerPlayer player, Object msg) {
        channel.sendToPlayer(player, msg);
    }

    public void sendToPlayers(List<ServerPlayer> player, Object msg) {
        channel.sendToPlayers(player, msg);
    }

    public void sendToServer(Object msg) {
        channel.sendToServer(msg);
    }
}
