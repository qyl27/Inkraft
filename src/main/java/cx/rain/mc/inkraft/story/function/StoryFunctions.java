package cx.rain.mc.inkraft.story.function;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.story.function.game.RealTimeFunction;
import cx.rain.mc.inkraft.story.function.game.WorldTimeFunction;
import cx.rain.mc.inkraft.story.function.game.command.RunCommandFunction;
import cx.rain.mc.inkraft.story.function.game.command.ScoreboardFunction;
import cx.rain.mc.inkraft.story.function.game.command.ScoreboardValuedFunction;
import cx.rain.mc.inkraft.story.function.game.command.storage.GetStorageFunction;
import cx.rain.mc.inkraft.story.function.game.command.storage.SetStorageFunction;
import cx.rain.mc.inkraft.story.function.game.inventory.CountItemFunction;
import cx.rain.mc.inkraft.story.function.game.inventory.GiveItemFunction;
import cx.rain.mc.inkraft.story.function.game.inventory.HasItemFunction;
import cx.rain.mc.inkraft.story.function.game.inventory.TakeItemFunction;
import cx.rain.mc.inkraft.story.function.game.player.GetPlayerNameFunction;
import cx.rain.mc.inkraft.story.function.system.*;
import cx.rain.mc.inkraft.story.function.system.flow.*;
import cx.rain.mc.inkraft.story.function.system.line.IsEndedFunction;
import cx.rain.mc.inkraft.story.function.system.line.PauseFunction;
import cx.rain.mc.inkraft.story.function.system.line.SetLineTicksFunction;
import cx.rain.mc.inkraft.story.function.system.line.UnsetLineTicksFunction;
import cx.rain.mc.inkraft.story.function.system.variable.*;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

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

    public static final RegistrySupplier<IStoryFunction> LOG_DEBUG = FUNCTIONS.register("log_debug", () -> new LogFunction("logDebug", Logger::debug));
    public static final RegistrySupplier<IStoryFunction> LOG_INFO = FUNCTIONS.register("log_info", () -> new LogFunction("logInfo", Logger::info));
    public static final RegistrySupplier<IStoryFunction> LOG_WARN = FUNCTIONS.register("log_warn", () -> new LogFunction("logWarn", Logger::warn));
    public static final RegistrySupplier<IStoryFunction> LOG_ERROR = FUNCTIONS.register("log_error", () -> new LogFunction("logError", Logger::error));

    /// </editor-fold>

    /// <editor-fold desc="Game functions.">

    public static final RegistrySupplier<IStoryFunction> GET_PLAYER_NAME = FUNCTIONS.register("get_player_name", GetPlayerNameFunction::new);
    public static final RegistrySupplier<IStoryFunction> GET_WORLD_DAY_TIME = FUNCTIONS.register("get_world_day_time", () -> new WorldTimeFunction("getWorldDayTime", level -> (int)(level.getDayTime() % 24000L)));
    public static final RegistrySupplier<IStoryFunction> GET_WORLD_GAME_TIME = FUNCTIONS.register("get_world_game_time", () -> new WorldTimeFunction("getWorldGameTime", level -> (int)(level.getGameTime() % Integer.MAX_VALUE)));
    public static final RegistrySupplier<IStoryFunction> GET_WORLD_DAY = FUNCTIONS.register("get_world_day", () -> new WorldTimeFunction("getWorldDay", level -> (int)(level.getDayTime() / 24000L % Integer.MAX_VALUE)));
    public static final RegistrySupplier<IStoryFunction> GET_REAL_TIME = FUNCTIONS.register("get_real_time", RealTimeFunction::new);

    public static final RegistrySupplier<IStoryFunction> RUN_COMMAND = FUNCTIONS.register("run_command", () -> new RunCommandFunction("runCommand", Entity::createCommandSourceStack));
    public static final RegistrySupplier<IStoryFunction> RUN_UNLIMITED_COMMAND = FUNCTIONS.register("run_unlimited_command", () -> new RunCommandFunction("runUnlimitedCommand", player -> player.createCommandSourceStack().withPermission(4)));
    public static final RegistrySupplier<IStoryFunction> RUN_SILENT_UNLIMITED_COMMAND = FUNCTIONS.register("run_silent_unlimited_command", () -> new RunCommandFunction("runSilentUnlimitedCommand", player -> player.createCommandSourceStack().withPermission(4).withSuppressedOutput()));
    public static final RegistrySupplier<IStoryFunction> RUN_SERVER_COMMAND = FUNCTIONS.register("run_server_command", () -> new RunCommandFunction("runServerCommand", player -> player.server.createCommandSourceStack().withPosition(player.position()).withRotation(player.getRotationVector()).withLevel(player.serverLevel())));

    public static final RegistrySupplier<IStoryFunction> GET_SCOREBOARD = FUNCTIONS.register("get_scoreboard", ScoreboardFunction::getScoreBoard);
    public static final RegistrySupplier<IStoryFunction> SET_SCOREBOARD = FUNCTIONS.register("set_scoreboard", ScoreboardValuedFunction::setScoreBoard);
    public static final RegistrySupplier<IStoryFunction> ADD_SCOREBOARD = FUNCTIONS.register("add_scoreboard", ScoreboardValuedFunction::addScoreBoard);
    public static final RegistrySupplier<IStoryFunction> SUB_SCOREBOARD = FUNCTIONS.register("sub_scoreboard", ScoreboardValuedFunction::subScoreBoard);
    public static final RegistrySupplier<IStoryFunction> MULTIPLY_SCOREBOARD = FUNCTIONS.register("multiply_scoreboard", ScoreboardValuedFunction::multiplyScoreBoard);

    public static final RegistrySupplier<IStoryFunction> GET_STORAGE = FUNCTIONS.register("get_storage", GetStorageFunction::new);
    public static final RegistrySupplier<IStoryFunction> SET_STORAGE = FUNCTIONS.register("set_storage", SetStorageFunction::new);

    public static final RegistrySupplier<IStoryFunction> HAS_ITEM = FUNCTIONS.register("has_item", HasItemFunction::new);
    public static final RegistrySupplier<IStoryFunction> COUNT_ITEM = FUNCTIONS.register("count_item", CountItemFunction::new);
    public static final RegistrySupplier<IStoryFunction> GIVE_ITEM = FUNCTIONS.register("give_item", GiveItemFunction::new);
    public static final RegistrySupplier<IStoryFunction> TAKE_ITEM = FUNCTIONS.register("take_item", TakeItemFunction::new);

    /// </editor-fold>
}
