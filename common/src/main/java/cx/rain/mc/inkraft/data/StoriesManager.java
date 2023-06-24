package cx.rain.mc.inkraft.data;

import com.bladecoder.ink.runtime.Story;
import cx.rain.mc.inkraft.story.StoryWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class StoriesManager {
    private final Map<ResourceLocation, String> stories = new HashMap<>();
    private final Map<UUID, StoryWrapper> cachedStories = new HashMap<>();

    public StoriesManager() {
    }

    public void addStory(ResourceLocation resourceLocation, String json) {
        try {
            stories.put(resourceLocation, json);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void clearStories() {
        stories.clear();
        refreshStories();
    }

    public Set<ResourceLocation> getStories() {
        return stories.keySet();
    }

    public boolean hasCachedStory(ServerPlayer player) {
        return cachedStories.containsKey(player.getUUID());
    }

    public StoryWrapper createStory(ServerPlayer player) {
        try {
            var storyWrapper = new StoryWrapper(this);
            cachedStories.put(player.getUUID(), storyWrapper);
            return storyWrapper;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void refreshStory(ServerPlayer player) {
        cachedStories.remove(player.getUUID());
    }

    public void refreshStories() {
        cachedStories.clear();
    }

    public StoryWrapper getStory(ServerPlayer player) {
        return cachedStories.get(player.getUUID());
    }

    public String getStoryString(ResourceLocation path) {
        return stories.get(path);
    }
}
