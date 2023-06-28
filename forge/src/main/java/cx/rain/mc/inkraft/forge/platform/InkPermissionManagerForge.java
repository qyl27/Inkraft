package cx.rain.mc.inkraft.forge.platform;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.command.IInkPermissionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

@Mod.EventBusSubscriber(modid = Inkraft.MODID)
public class InkPermissionManagerForge implements IInkPermissionManager {

    public static final PermissionNode<Boolean> PERMISSION_START = bool("start", 2);
    public static final PermissionNode<Boolean> PERMISSION_CONTINUE = bool("continue", 0);
    public static final PermissionNode<Boolean> PERMISSION_CLEAR = bool("clear", 2);
    public static final PermissionNode<Boolean> PERMISSION_START_OTHER = bool("start.other", 2);
    public static final PermissionNode<Boolean> PERMISSION_CONTINUE_OTHER = bool("continue.other", 2);

    private static PermissionNode<Boolean> bool(String name, int defaultLevel) {
        return new PermissionNode<>(Inkraft.MODID, name, PermissionTypes.BOOLEAN,
                (player, uuid, context) -> player != null && player.hasPermissions(defaultLevel));
    }

    @SubscribeEvent
    public static void registerPermission(PermissionGatherEvent.Nodes event) {
        event.addNodes(PERMISSION_START);
        event.addNodes(PERMISSION_CONTINUE);
        event.addNodes(PERMISSION_CLEAR);
        event.addNodes(PERMISSION_START_OTHER);
        event.addNodes(PERMISSION_CONTINUE_OTHER);
    }

    private boolean check(CommandSourceStack source, PermissionNode<Boolean> node) {
        if (source.getEntity() instanceof ServerPlayer player) {
            return PermissionAPI.getPermission(player, node);
        } else {
            return true;
        }
    }

    @Override
    public boolean hasStartPermission(CommandSourceStack source) {
        return check(source, PERMISSION_START);
    }

    @Override
    public boolean hasContinuePermission(CommandSourceStack source) {
        return check(source, PERMISSION_CONTINUE);
    }

    @Override
    public boolean hasClearPermission(CommandSourceStack source) {
        return check(source, PERMISSION_CLEAR);
    }

    @Override
    public boolean hasStartForOtherPermission(CommandSourceStack source) {
        return check(source, PERMISSION_START_OTHER);
    }

    @Override
    public boolean hasContinueForOtherPermission(CommandSourceStack source) {
        return check(source, PERMISSION_CONTINUE_OTHER);
    }
}
