package cx.rain.mc.inkraft.data;

import com.bladecoder.ink.runtime.Story;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class StoriesManager {
    private Map<ResourceLocation, String> stories = new HashMap<>();
    private Map<UUID, Story> cachedStories = new HashMap<>();

    public StoriesManager() {
        // Todo: load compiled ink file.
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

    public Story getStory(ServerPlayer player, ResourceLocation path) {
        try {
            var uuid = player.getUUID();
            if (cachedStories.containsKey(uuid)) {
                return cachedStories.get(uuid);
            }

            var story = new Story(stories.get(path));
            cachedStories.put(uuid, story);
            return story;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
