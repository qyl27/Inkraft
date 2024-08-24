package cx.rain.mc.inkraft.story.function;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.story.function.game.command.RunCommandFunction;
import cx.rain.mc.inkraft.story.function.game.player.GetPlayerNameFunction;
import cx.rain.mc.inkraft.story.function.system.*;
import cx.rain.mc.inkraft.story.function.system.flow.*;
import cx.rain.mc.inkraft.story.function.system.line.IsEndedFunction;
import cx.rain.mc.inkraft.story.function.system.line.PauseFunction;
import cx.rain.mc.inkraft.story.function.system.line.SetLineTicksFunction;
import cx.rain.mc.inkraft.story.function.system.line.UnsetLineTicksFunction;
import cx.rain.mc.inkraft.story.function.system.logging.LogDebugFunction;
import cx.rain.mc.inkraft.story.function.system.logging.LogErrorFunction;
import cx.rain.mc.inkraft.story.function.system.logging.LogInfoFunction;
import cx.rain.mc.inkraft.story.function.system.logging.LogWarnFunction;
import cx.rain.mc.inkraft.story.function.system.variable.*;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class StoryFunctions {
    public static final ResourceLocation REGISTRY_NAME = ResourceLocation.fromNamespaceAndPath(Inkraft.MODID, "functions");
    public static final ResourceKey<Registry<IStoryFunction>> REGISTRY_KEY = ResourceKey.createRegistryKey(REGISTRY_NAME);
    public static final Registrar<IStoryFunction> REGISTRAR = RegistrarManager.get(Inkraft.MODID).<IStoryFunction>builder(REGISTRY_NAME).build(); // qyl27: Architectury API needs this line to create registry.

    public static final DeferredRegister<IStoryFunction> FUNCTIONS = DeferredRegister.create(Inkraft.MODID, REGISTRY_KEY);

    public static void register() {
        FUNCTIONS.register();
    }

    /// <editor-fold desc="System functions.">

    public static final RegistrySupplier<IStoryFunction> IS_DEBUG = FUNCTIONS.register("is_debug", IsDebugFunction::new);

    public static final RegistrySupplier<IStoryFunction> NEW_FLOW = FUNCTIONS.register("new_flow", NewFlowFunction::new);
    public static final RegistrySupplier<IStoryFunction> REMOVE_FLOW = FUNCTIONS.register("remove_flow", RemoveFlowFunction::new);
    public static final RegistrySupplier<IStoryFunction> FLOW_TO = FUNCTIONS.register("flow_to", FlowToFunction::new);
    public static final RegistrySupplier<IStoryFunction> FLOW_TO_DEFAULT = FUNCTIONS.register("flow_to_default", FlowToDefaultFunction::new);
    public static final RegistrySupplier<IStoryFunction> IS_IN_FLOW = FUNCTIONS.register("is_in_flow", IsInFlowFunction::new);
    public static final RegistrySupplier<IStoryFunction> IS_IN_DEFAULT_FLOW = FUNCTIONS.register("is_in_default_flow", IsInDefaultFlowFunction::new);

    public static final RegistrySupplier<IStoryFunction> IS_ENDED = FUNCTIONS.register("is_ended", IsEndedFunction::new);
    public static final RegistrySupplier<IStoryFunction> PAUSE = FUNCTIONS.register("pause", PauseFunction::new);
    public static final RegistrySupplier<IStoryFunction> SET_LINE_TICKS = FUNCTIONS.register("set_line_ticks", SetLineTicksFunction::new);
    public static final RegistrySupplier<IStoryFunction> UNSET_LINE_TICKS = FUNCTIONS.register("unset_line_ticks", UnsetLineTicksFunction::new);

    public static final RegistrySupplier<IStoryFunction> HAS_VARIABLE = FUNCTIONS.register("has_variable", HasVariableFunction::new);
    public static final RegistrySupplier<IStoryFunction> GET_VARIABLE = FUNCTIONS.register("get_variable", GetVariableFunction::new);
    public static final RegistrySupplier<IStoryFunction> SET_VARIABLE = FUNCTIONS.register("set_variable", SetVariableFunction::new);
    public static final RegistrySupplier<IStoryFunction> UNSET_VARIABLE = FUNCTIONS.register("unset_variable", UnsetVariableFunction::new);
    public static final RegistrySupplier<IStoryFunction> CLEAR_VARIABLE = FUNCTIONS.register("clear_variable", ClearVariableFunction::new);

    public static final RegistrySupplier<IStoryFunction> LOG_DEBUG = FUNCTIONS.register("log_debug", LogDebugFunction::new);
    public static final RegistrySupplier<IStoryFunction> LOG_INFO = FUNCTIONS.register("log_info", LogInfoFunction::new);
    public static final RegistrySupplier<IStoryFunction> LOG_WARN = FUNCTIONS.register("log_warn", LogWarnFunction::new);
    public static final RegistrySupplier<IStoryFunction> LOG_ERROR = FUNCTIONS.register("log_error", LogErrorFunction::new);

    /// </editor-fold>

    /// <editor-fold desc="Game functions.">
    public static final RegistrySupplier<IStoryFunction> GET_PLAYER_NAME = FUNCTIONS.register("get_player_name", GetPlayerNameFunction::new);

    public static final RegistrySupplier<IStoryFunction> RUN_COMMAND = FUNCTIONS.register("run_command", RunCommandFunction::new);
    public static final RegistrySupplier<IStoryFunction> RUN_UNLIMITED_COMMAND = FUNCTIONS.register("run_unlimited_command", RunCommandFunction.UnlimitedCommand::new);
    public static final RegistrySupplier<IStoryFunction> RUN_SERVER_COMMAND = FUNCTIONS.register("run_server_command", RunCommandFunction.ServerCommand::new);

//    public static final RegistrySupplier<StoryFunction> GIVE_ITEM = FUNCTIONS.register("give_item", GiveItemFunction::new);
//    public static final RegistrySupplier<StoryFunction> GIVE_ITEM_STACK = FUNCTIONS.register("give_item_stack", GiveItemStackFunction::new);
//    public static final RegistrySupplier<StoryFunction> HAS_ITEM_STACK = FUNCTIONS.register("has_item_stack", HasItemStackFunction::new);
//    public static final RegistrySupplier<StoryFunction> COUNT_ITEM_STACK = FUNCTIONS.register("count_item_stack", CountItemStackFunction::new);
//    public static final RegistrySupplier<StoryFunction> TAKE_ITEM_STACK = FUNCTIONS.register("take_item_stack", TakeItemStackFunction::new);

    /// </editor-fold>
}
