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

    public Set<ResourceLocation> getStories() {
        return stories.keySet();
    }

    public StoryWrapper getStory(ServerPlayer player, ResourceLocation path) {
        try {
            var uuid = player.getUUID();
            if (cachedStories.containsKey(uuid)) {
                return cachedStories.get(uuid);
            }

            var story = new StoryWrapper(new Story(stories.get(path)));
            cachedStories.put(uuid, story);
            return story;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
