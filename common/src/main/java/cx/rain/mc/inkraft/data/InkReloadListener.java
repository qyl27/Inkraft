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
        backgroundExecutor.execute(() -> scan(resourceManager, STORY_PATH));
        return preparationBarrier.wait(null);
    }

    public void scan(ResourceManager resourceManager, String dir) {
        FileToIdConverter fileToIdConverter = FileToIdConverter.json(dir);

        for (Entry<ResourceLocation, Resource> entry : fileToIdConverter.listMatchingResources(resourceManager).entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();
            ResourceLocation id = fileToIdConverter.fileToId(resourceLocation);

            try {
                Inkraft.getInstance().getStoriesManager().addStory(id, IOUtils.toString(entry.getValue().open()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public String getName() {
        return "InkStoryReloadListener";
    }
}
