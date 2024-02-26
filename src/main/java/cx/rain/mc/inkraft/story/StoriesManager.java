package cx.rain.mc.inkraft.story;

import com.bladecoder.ink.runtime.Story;
import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.InkraftPlatform;
import cx.rain.mc.inkraft.networking.packet.S2CHideAllVariablePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class StoriesManager {
    private final Map<ResourceLocation, String> loadedStories = new HashMap<>();

//    private final Map<UUID, PlayerStory> cachedStories = new HashMap<>();

    public StoriesManager() {
    }

    public void addStory(ResourceLocation resourceLocation, String json) {
        try {
            loadedStories.put(resourceLocation, json);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void clearStories() {
        loadedStories.clear();
//        refreshStories();
    }

    public Set<ResourceLocation> getLoadedStories() {
        return loadedStories.keySet();
    }

//    public boolean hasCachedStory(ServerPlayer player) {
//        return cachedStories.containsKey(player.getUUID());
//    }

    public PlayerStory createStory(ServerPlayer player, ResourceLocation path) {
        try {
            var story = new Story(getStoryString(path));
            var playerStory = new PlayerStory(player, story);
//            cachedStories.put(player.getUUID(), playerStory);
            return playerStory;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

//    public void refreshStory(ServerPlayer player) {
//        cachedStories.remove(player.getUUID());
//        Inkraft.getInstance().getNetworking().sendToPlayer(player, new S2CHideAllVariablePacket());
//    }

//    public void refreshStories() {
//        cachedStories.clear();
//    }

    public PlayerStory getStory(ServerPlayer player) {
        return cachedStories.get(player.getUUID());
    }

    private String getStoryString(ResourceLocation path) {
        return loadedStories.get(path);
    }
}
