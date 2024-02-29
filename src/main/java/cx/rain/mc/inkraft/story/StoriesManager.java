package cx.rain.mc.inkraft.story;

import com.bladecoder.ink.runtime.Story;
import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.story.function.StoryFunctions;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StoriesManager {
    private final Map<ResourceLocation, String> loadedStories = new HashMap<>();

    public StoriesManager() {
    }

    public void addStory(ResourceLocation resourceLocation, String json) {
        loadedStories.put(resourceLocation, json);
    }

    public void clearStories() {
        loadedStories.clear();
    }

    public Set<ResourceLocation> getLoadedStories() {
        return loadedStories.keySet();
    }

//    public boolean hasCachedStory(ServerPlayer player) {
//        return cachedStories.containsKey(player.getUUID());
//    }

//    public PlayerStory createStory(ServerPlayer player, ResourceLocation path) {
////        try {
////            var story = new Story(getStoryString(path));
////            var playerStory = new PlayerStory(player, story);
//////            cachedStories.put(player.getUUID(), playerStory);
////            return playerStory;
////        } catch (Exception ex) {
////            throw new RuntimeException(ex);
////        }
//    }

//    public void refreshStory(ServerPlayer player) {
//        cachedStories.remove(player.getUUID());
//        Inkraft.getInstance().getNetworking().sendToPlayer(player, new S2CHideAllVariablePacket());
//    }

//    public void refreshStories() {
//        cachedStories.clear();
//    }

    public Story createStory(ResourceLocation path) {
        var str = getStoryString(path);
        try {
            return new Story(str);
        } catch (Exception ex) {
            Inkraft.getInstance().getLogger().error("Failed to load story " + path, ex);
            throw new RuntimeException(ex);
        }
    }

    private String getStoryString(ResourceLocation path) {
        return loadedStories.get(path);
    }
}
