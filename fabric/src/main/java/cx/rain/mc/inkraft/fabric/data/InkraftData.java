package cx.rain.mc.inkraft.fabric.data;

import cx.rain.mc.inkraft.platform.impl.InkPlayerData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InkraftData extends SavedData {

    private static final Factory<InkraftData> FACTORY = new Factory<>(InkraftData::new, InkraftData::from, null);

    private static InkraftData data;

    public static InkraftData get(@Nullable MinecraftServer server) {
        if (server == null) {
            return new InkraftData();
        }

        if (data == null) {
            data = server.overworld().getDataStorage().get(FACTORY, "inkraft_data");
        }

        return data;
    }

    private final Map<UUID, InkPlayerData> playersData = new HashMap<>();

    public InkPlayerData get(Player player) {
        var data = playersData.computeIfAbsent(player.getUUID(), u -> new InkPlayerData());
        setDirty();
        return data;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        var list = new ListTag();

        for (var e : playersData.entrySet()) {
            var t = new CompoundTag();
            t.putUUID("uuid", e.getKey());
            t.put("data", e.getValue().serialize());
            list.add(t);
        }

        tag.put("playersData", list);
        return tag;
    }

    private static InkraftData from(CompoundTag tag, HolderLookup.Provider registries) {
        var result = new InkraftData();
        if (tag.contains("playersData")) {
            var list = tag.getList("playersData", ListTag.TAG_COMPOUND);
            for (var i = 0; i < list.size(); i++) {
                var t = list.getCompound(i);
                if (!t.contains("uuid") || !t.contains("data")) {
                    continue;
                }

                var uuid = t.getUUID("uuid");
                var data = new InkPlayerData();
                data.deserialize(t.getCompound("data"));
                result.playersData.put(uuid, data);
            }
        }
        return result;
    }
}
