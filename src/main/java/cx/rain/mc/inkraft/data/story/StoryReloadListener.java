package cx.rain.mc.inkraft.data.story;

import cx.rain.mc.inkraft.Inkraft;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class StoryReloadListener implements PreparableReloadListener {
    public static final ResourceLocation INKRAFT_STORY_LOADER = ResourceLocation.fromNamespaceAndPath(Inkraft.MODID, "story_loader");

    public static final String STORY_PATH = "inkraft_story";
    public static final FileToIdConverter FILE_TO_ID_CONVERTER = new FileToIdConverter(STORY_PATH, ".ink.json");

    private final IDataRegistry<ResourceLocation, String> registry;

    public StoryReloadListener(IDataRegistry<ResourceLocation, String> registry) {
        this.registry = registry;
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier,
                                                   ResourceManager resourceManager,
                                                   ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler,
                                                   Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture
                .supplyAsync(() -> prepare(resourceManager, preparationsProfiler), backgroundExecutor)
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(stories -> apply(stories, reloadProfiler), gameExecutor);
    }

    private Map<ResourceLocation, String> prepare(ResourceManager resourceManager, ProfilerFiller preparationsProfiler) {
        preparationsProfiler.startTick();
        preparationsProfiler.push("inkraft");

        var stories = new HashMap<ResourceLocation, String>();
        FILE_TO_ID_CONVERTER.listMatchingResources(resourceManager).forEach((path, resource) -> {
            try {
                stories.put(FILE_TO_ID_CONVERTER.fileToId(path),
                        IOUtils.toString(resource.open(), StandardCharsets.UTF_8));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        preparationsProfiler.pop();
        preparationsProfiler.endTick();
        return stories;
    }

    private void apply(Map<ResourceLocation, String> stories, ProfilerFiller reloadProfiler) {
        reloadProfiler.startTick();
        reloadProfiler.push("inkraft");

        registry.clear();
        stories.forEach(registry::add);

        reloadProfiler.pop();
        reloadProfiler.endTick();
    }

    @Override
    public @NotNull String getName() {
        return "StoryReloadListener";
    }
}
