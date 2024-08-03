package cx.rain.mc.inkraft.story.function;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.story.function.system.*;
import cx.rain.mc.inkraft.story.function.system.flow.FlowToDefaultFunction;
import cx.rain.mc.inkraft.story.function.system.flow.FlowToFunction;
import cx.rain.mc.inkraft.story.function.system.flow.IsInDefaultFlowFunction;
import cx.rain.mc.inkraft.story.function.system.flow.IsInFlowFunction;
import cx.rain.mc.inkraft.story.function.system.line.IsEndedFunction;
import cx.rain.mc.inkraft.story.function.system.line.PauseFunction;
import cx.rain.mc.inkraft.story.function.system.line.SetLineTicksFunction;
import cx.rain.mc.inkraft.story.function.system.line.UnsetLineTicksFunction;
import cx.rain.mc.inkraft.story.function.system.variable.ClearVariableFunction;
import cx.rain.mc.inkraft.story.function.system.variable.GetVariableFunction;
import cx.rain.mc.inkraft.story.function.system.variable.SetVariableFunction;
import cx.rain.mc.inkraft.story.function.system.variable.UnsetVariableFunction;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class StoryFunctions {
    public static final ResourceLocation REGISTRY_NAME = ResourceLocation.fromNamespaceAndPath(Inkraft.MODID, "functions");
    public static final ResourceKey<Registry<StoryFunction>> REGISTRY_KEY = ResourceKey.createRegistryKey(REGISTRY_NAME);

    public static final DeferredRegister<StoryFunction> FUNCTIONS = DeferredRegister.create(Inkraft.MODID, REGISTRY_KEY);

    public static void register() {
        FUNCTIONS.register();
    }

    public static final RegistrySupplier<StoryFunction> IS_DEBUG = FUNCTIONS.register("is_debug", IsDebugFunction::new);

    public static final RegistrySupplier<StoryFunction> FLOW_TO = FUNCTIONS.register("flow_to", FlowToFunction::new);
    public static final RegistrySupplier<StoryFunction> FLOW_TO_DEFAULT = FUNCTIONS.register("flow_to_default", FlowToDefaultFunction::new);
    public static final RegistrySupplier<StoryFunction> IS_IN_FLOW = FUNCTIONS.register("is_in_flow", IsInFlowFunction::new);
    public static final RegistrySupplier<StoryFunction> IS_IN_DEFAULT_FLOW = FUNCTIONS.register("is_in_default_flow", IsInDefaultFlowFunction::new);

    public static final RegistrySupplier<StoryFunction> IS_ENDED = FUNCTIONS.register("is_ended", IsEndedFunction::new);
    public static final RegistrySupplier<StoryFunction> PAUSE = FUNCTIONS.register("pause", PauseFunction::new);
    public static final RegistrySupplier<StoryFunction> SET_LINE_TICKS = FUNCTIONS.register("set_line_ticks", SetLineTicksFunction::new);
    public static final RegistrySupplier<StoryFunction> UNSET_LINE_TICKS = FUNCTIONS.register("unset_line_ticks", UnsetLineTicksFunction::new);

    public static final RegistrySupplier<StoryFunction> GET_VARIABLE = FUNCTIONS.register("get_variable", GetVariableFunction::new);
    public static final RegistrySupplier<StoryFunction> SET_VARIABLE = FUNCTIONS.register("set_variable", SetVariableFunction::new);
    public static final RegistrySupplier<StoryFunction> UNSET_VARIABLE = FUNCTIONS.register("unset_variable", UnsetVariableFunction::new);
    public static final RegistrySupplier<StoryFunction> CLEAR_VARIABLE = FUNCTIONS.register("clear_variable", ClearVariableFunction::new);

//    public static final RegistrySupplier<StoryFunction> RUN_COMMAND = FUNCTIONS.register("run_command", RunCommandFunction::new);
//    public static final RegistrySupplier<StoryFunction> GET_PLAYER_NAME = FUNCTIONS.register("get_player_name", GetPlayerNameFunction::new);
//    public static final RegistrySupplier<StoryFunction> GIVE_ITEM = FUNCTIONS.register("give_item", GiveItemFunction::new);
//    public static final RegistrySupplier<StoryFunction> GIVE_ITEM_STACK = FUNCTIONS.register("give_item_stack", GiveItemStackFunction::new);
//    public static final RegistrySupplier<StoryFunction> HAS_ITEM_STACK = FUNCTIONS.register("has_item_stack", HasItemStackFunction::new);
//    public static final RegistrySupplier<StoryFunction> COUNT_ITEM_STACK = FUNCTIONS.register("count_item_stack", CountItemStackFunction::new);
//    public static final RegistrySupplier<StoryFunction> TAKE_ITEM_STACK = FUNCTIONS.register("take_item_stack", TakeItemStackFunction::new);
}
