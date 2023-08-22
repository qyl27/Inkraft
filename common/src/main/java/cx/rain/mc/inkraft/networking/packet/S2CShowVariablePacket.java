package cx.rain.mc.inkraft.networking.packet;

import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.utility.StoryVariables;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class S2CShowVariablePacket {
    private String name;
    private String displayName;
    private boolean isShow;
    private String value;

    public S2CShowVariablePacket(String name, String displayName, boolean isShow, String value) {
        this.name = name;
        this.displayName = displayName;
        this.isShow = isShow;
        this.value = value;
    }

    public S2CShowVariablePacket(FriendlyByteBuf buf) {
        this(buf.readUtf(), buf.readUtf(), buf.readBoolean(), buf.readUtf());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeUtf(displayName);
        buf.writeBoolean(isShow);
        buf.writeUtf(value);
    }

    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        var context = contextSupplier.get();
        context.queue(() -> InkraftPlatform.getPlayerStoryStateHolder(context.getPlayer()).putVariable(name, displayName, isShow, StoryVariables.IStoryVariable.fromString(value)));
    }
}
