package cx.rain.mc.inkraft.story.function;

import cx.rain.mc.inkraft.Inkraft;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class StoryFunctions {
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(Inkraft.MODID, "story_functions");
    public static final ResourceKey<Registry<StoryFunction>> REGISTRY_KEY = ResourceKey.createRegistryKey(REGISTRY_NAME);
    public static final Registrar<StoryFunction> REGISTRAR = RegistrarManager.get(Inkraft.MODID).<StoryFunction>builder(REGISTRY_NAME).build();

    public static final DeferredRegister<StoryFunction> FUNCTIONS = DeferredRegister.create(Inkraft.MODID, REGISTRY_KEY);

    public static void register() {
        FUNCTIONS.register();
    }

    public static final RegistrySupplier<StoryFunction> GET_PLAYER_NAME = FUNCTIONS.register("get_player_name", GetPlayerNameFunction::new);
    public static final RegistrySupplier<StoryFunction> RUN_COMMAND = FUNCTIONS.register("run_command", RunCommandFunction::new);
    public static final RegistrySupplier<StoryFunction> IS_DEBUG = FUNCTIONS.register("is_debug", IsDebugFunction::new);
}
