package cx.rain.mc.inkraft.data.story;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.engine.EngineManager;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
public class StoryReloadListener implements PreparableReloadListener {
    public static final Identifier INKRAFT_STORY_LOADER = Identifier.fromNamespaceAndPath(Inkraft.MODID, "story_loader");

    public static final StoryReloadListener INSTANCE = new StoryReloadListener(EngineManager.getInstance().getStoryRegistry());

    protected static final String STORY_PATH = "inkraft_story";
    protected static final FileToIdConverter FILE_TO_ID_CONVERTER = new FileToIdConverter(STORY_PATH, ".ink.json");

    private final IDataRegistry<Identifier, String> registry;

    protected StoryReloadListener(IDataRegistry<Identifier, String> registry) {
        this.registry = registry;
    }

    @Override
    public CompletableFuture<Void> reload(SharedState currentReload,
                                          Executor taskExecutor,
                                          PreparationBarrier preparationBarrier,
                                          Executor reloadExecutor) {
        return CompletableFuture
            .supplyAsync(() -> prepare(currentReload.resourceManager()), taskExecutor)
            .thenCompose(preparationBarrier::wait)
            .thenAcceptAsync(this::apply, reloadExecutor);
    }

    private Map<Identifier, String> prepare(ResourceManager resourceManager) {
        var stories = new HashMap<Identifier, String>();
        FILE_TO_ID_CONVERTER.listMatchingResources(resourceManager).forEach((path, resource) -> {
            var id = FILE_TO_ID_CONVERTER.fileToId(path);
            try (var input = resource.open()) {
                var content = IOUtils.toString(input, StandardCharsets.UTF_8);
                stories.put(id, content);
            } catch (IOException ex) {
                log.error("Failed to load story {} from pack {}",
                    id, resource.sourcePackId(), ex);
            }
        });
        return stories;
    }

    private void apply(Map<Identifier, String> stories) {
        registry.clear();
        stories.forEach(registry::add);
    }

    @Override
    public String getName() {
        return "StoryReloadListener";
    }
}
