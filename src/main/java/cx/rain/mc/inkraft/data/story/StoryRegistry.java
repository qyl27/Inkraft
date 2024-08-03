package cx.rain.mc.inkraft.data.story;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StoryRegistry implements IDataRegistry<ResourceLocation, String> {
    private final Map<ResourceLocation, String> stories = new HashMap<>();

    public StoryRegistry() {
    }

    @Override
    public void add(ResourceLocation resourceLocation, String json) {
        stories.put(resourceLocation, json);
    }

    @Override
    public void clear() {
        stories.clear();
    }

    @Override
    public Set<ResourceLocation> getAll() {
        return stories.keySet();
    }

    @Override
    public String get(ResourceLocation path) {
        return stories.get(path);
    }
}
