package cx.rain.mc.inkraft.data;

import cx.rain.mc.inkraft.Inkraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class InkReloadListener implements PreparableReloadListener {

    public static final ResourceLocation INKRAFT_STORY = new ResourceLocation(Inkraft.MODID, "stories");
    public static final String STORY_PATH = "stories";

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager,
                                          ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler,
                                          Executor backgroundExecutor, Executor gameExecutor) {
        var files = resourceManager.listResources(STORY_PATH, rl -> rl.getPath().endsWith(".json"));

        for (var file : files.entrySet()) {
            try {
                Inkraft.getInstance().getStoriesManager().addStory(file.getKey(), IOUtils.toString(file.getValue().open()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        var completableFuture = new CompletableFuture<Void>();
        completableFuture.complete(null);
        return completableFuture;
    }

    @Override
    public String getName() {
        return "InkStoryReloadListener";
    }
}
