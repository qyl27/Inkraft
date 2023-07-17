package cx.rain.mc.inkraft.networking.packet;

import cx.rain.mc.inkraft.InkraftPlatform;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class S2CHideAllVariablePacket {
    public S2CHideAllVariablePacket() {
    }

    public S2CHideAllVariablePacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        var context = contextSupplier.get();
        context.queue(() -> InkraftPlatform.getPlayerStoryStateHolder(context.getPlayer()).clearShowedVariables());
    }
}
