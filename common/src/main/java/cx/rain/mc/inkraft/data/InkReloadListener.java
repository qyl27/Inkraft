package cx.rain.mc.inkraft.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import cx.rain.mc.inkraft.Inkraft;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class InkReloadListener implements PreparableReloadListener {

    public static final String STORY_PATH = "stories";
    public static final ResourceLocation INKRAFT_STORY = new ResourceLocation(Inkraft.MODID, STORY_PATH);

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager,
                                          ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler,
                                          Executor backgroundExecutor, Executor gameExecutor) {
        preparationsProfiler.startTick();
        preparationsProfiler.endTick();
        return prepare(resourceManager, backgroundExecutor)
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(results -> apply(results, reloadProfiler), gameExecutor);
    }

    private CompletableFuture<Map<ResourceLocation, String>> prepare(ResourceManager resourceManager,
                                                                     Executor backgroundExecutor) {
        var stories = new HashMap<ResourceLocation, String>();
        return CompletableFuture
                .runAsync(() -> scan(resourceManager, stories), backgroundExecutor)
                .thenApply(__ -> stories);
    }

    private void scan(ResourceManager resourceManager, Map<ResourceLocation, String> stories) {
        FileToIdConverter fileToIdConverter = new FileToIdConverter(STORY_PATH, ".ink.json");

        for (Entry<ResourceLocation, Resource> entry : fileToIdConverter.listMatchingResources(resourceManager).entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();
            ResourceLocation id = fileToIdConverter.fileToId(resourceLocation);

            try {
                stories.put(id, IOUtils.toString(entry.getValue().open()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void apply(Map<ResourceLocation, String> stories, ProfilerFiller reloadProfiler) {
        reloadProfiler.startTick();
        reloadProfiler.push("stories");

        Inkraft.getInstance().getStoriesManager().clearStories();

        for (var story : stories.entrySet()) {
            Inkraft.getInstance().getStoriesManager().addStory(story.getKey(), story.getValue());
        }

        reloadProfiler.pop();
        reloadProfiler.endTick();
    }

    @Override
    public String getName() {
        return "InkStoryReloadListener";
    }
}
