package cx.rain.mc.inkraft.data;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class StoriesManager {
    private Map<ResourceLocation, String> stories = new HashMap<>();

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


}
