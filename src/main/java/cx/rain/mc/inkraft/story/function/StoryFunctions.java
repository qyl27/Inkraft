package cx.rain.mc.inkraft.story.function;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.story.function.game.*;
import cx.rain.mc.inkraft.story.function.system.*;
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

    public static final RegistrySupplier<StoryFunction> IS_DEBUG = FUNCTIONS.register("is_debug", IsDebugFunction::new);
    public static final RegistrySupplier<StoryFunction> IS_IN_STORY = FUNCTIONS.register("is_in_story", IsInStoryFunction::new);
    public static final RegistrySupplier<StoryFunction> SET_AUTO_CONTINUE = FUNCTIONS.register("set_auto_continue", SetAutoContinueFunction::new);
    public static final RegistrySupplier<StoryFunction> SET_AUTO_CONTINUE_SPEED = FUNCTIONS.register("set_auto_continue_speed", SetAutoContinueSpeedFunction::new);
    public static final RegistrySupplier<StoryFunction> PUT_VARIABLE = FUNCTIONS.register("put_variable", PutVariableFunction::new);
    public static final RegistrySupplier<StoryFunction> GET_VARIABLE = FUNCTIONS.register("get_variable", GetVariableFunction::new);
    public static final RegistrySupplier<StoryFunction> SHOW_VARIABLE = FUNCTIONS.register("show_variable", ShowVariableFunction::new);
    public static final RegistrySupplier<StoryFunction> HIDE_CONTINUE = FUNCTIONS.register("hide_continue", HideContinueFunction::new);

    public static final RegistrySupplier<StoryFunction> RUN_COMMAND = FUNCTIONS.register("run_command", RunCommandFunction::new);
    public static final RegistrySupplier<StoryFunction> GET_PLAYER_NAME = FUNCTIONS.register("get_player_name", GetPlayerNameFunction::new);
    public static final RegistrySupplier<StoryFunction> GIVE_ITEM = FUNCTIONS.register("give_item", GiveItemFunction::new);
    public static final RegistrySupplier<StoryFunction> GIVE_ITEM_STACK = FUNCTIONS.register("give_item_stack", GiveItemStackFunction::new);
    public static final RegistrySupplier<StoryFunction> HAS_ITEM_STACK = FUNCTIONS.register("has_item_stack", HasItemStackFunction::new);
    public static final RegistrySupplier<StoryFunction> COUNT_ITEM_STACK = FUNCTIONS.register("count_item_stack", CountItemStackFunction::new);
    public static final RegistrySupplier<StoryFunction> TAKE_ITEM_STACK = FUNCTIONS.register("take_item_stack", TakeItemStackFunction::new);
}
