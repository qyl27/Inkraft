package cx.rain.mc.inkraft.neoforge.registry;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.registry.InkraftRegistries;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
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
import cx.rain.mc.inkraft.story.function.system.IsDebugFunction;
import cx.rain.mc.inkraft.story.function.system.LogFunction;
import cx.rain.mc.inkraft.story.function.lang.ArrayFunctions;
import cx.rain.mc.inkraft.story.function.lang.MapFunctions;
import cx.rain.mc.inkraft.story.function.system.flow.*;
import cx.rain.mc.inkraft.story.function.system.line.PauseFunction;
import cx.rain.mc.inkraft.story.function.system.line.SetLineTicksFunction;
import cx.rain.mc.inkraft.story.function.system.line.UnsetLineTicksFunction;
import cx.rain.mc.inkraft.story.function.system.parse.ParseBoolFunction;
import cx.rain.mc.inkraft.story.function.system.parse.ParseFloatFunction;
import cx.rain.mc.inkraft.story.function.system.parse.ParseIntFunction;
import cx.rain.mc.inkraft.story.function.system.parse.ToStringFunction;
import cx.rain.mc.inkraft.story.function.system.variable.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class ModStoryFunctions {
    public static final DeferredRegister<IStoryFunction> REGISTRY = DeferredRegister.create(InkraftRegistries.STORY_FUNCTIONS, Inkraft.MODID);

    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
    }

    // <editor-fold desc="System functions.">

    public static final Supplier<IStoryFunction> IS_DEBUG = REGISTRY.register("is_debug", IsDebugFunction::new);

    public static final Supplier<IStoryFunction> NEW_FLOW = REGISTRY.register("new_flow", NewFlowFunction::new);
    public static final Supplier<IStoryFunction> REMOVE_FLOW = REGISTRY.register("remove_flow", RemoveFlowFunction::new);
    public static final Supplier<IStoryFunction> FLOW_TO = REGISTRY.register("flow_to", FlowToFunction::new);
    public static final Supplier<IStoryFunction> IS_IN_FLOW = REGISTRY.register("is_in_flow", IsInFlowFunction::new);
    public static final Supplier<IStoryFunction> HAS_FLOW = REGISTRY.register("has_flow", HasFlowFunction::new);
    public static final Supplier<IStoryFunction> IS_FLOW_ENDED = REGISTRY.register("is_flow_ended", IsFlowEndedFunction::new);

    public static final Supplier<IStoryFunction> PAUSE = REGISTRY.register("pause", PauseFunction::new);
    public static final Supplier<IStoryFunction> SET_LINE_TICKS = REGISTRY.register("set_line_ticks", SetLineTicksFunction::new);
    public static final Supplier<IStoryFunction> UNSET_LINE_TICKS = REGISTRY.register("unset_line_ticks", UnsetLineTicksFunction::new);

    public static final Supplier<IStoryFunction> HAS_VARIABLE = REGISTRY.register("has_variable", HasVariableFunction::new);
    public static final Supplier<IStoryFunction> GET_VARIABLE = REGISTRY.register("get_variable", GetVariableFunction::new);
    public static final Supplier<IStoryFunction> SET_VARIABLE = REGISTRY.register("set_variable", SetVariableFunction::new);
    public static final Supplier<IStoryFunction> UNSET_VARIABLE = REGISTRY.register("unset_variable", UnsetVariableFunction::new);
    public static final Supplier<IStoryFunction> CLEAR_VARIABLE = REGISTRY.register("clear_variable", ClearVariableFunction::new);

    public static final Supplier<IStoryFunction> LOG_DEBUG = REGISTRY.register("log_debug", () -> new LogFunction("logDebug", Logger::debug));
    public static final Supplier<IStoryFunction> LOG_INFO = REGISTRY.register("log_info", () -> new LogFunction("logInfo", Logger::info));
    public static final Supplier<IStoryFunction> LOG_WARN = REGISTRY.register("log_warn", () -> new LogFunction("logWarn", Logger::warn));
    public static final Supplier<IStoryFunction> LOG_ERROR = REGISTRY.register("log_error", () -> new LogFunction("logError", Logger::error));

    public static final Supplier<IStoryFunction> PARSE_BOOL = REGISTRY.register("parse_bool", ParseBoolFunction::new);
    public static final Supplier<IStoryFunction> PARSE_INT = REGISTRY.register("parse_int", ParseIntFunction::new);
    public static final Supplier<IStoryFunction> PARSE_FLOAT = REGISTRY.register("parse_float", ParseFloatFunction::new);
    public static final Supplier<IStoryFunction> TO_STRING = REGISTRY.register("to_string", ToStringFunction::new);

    public static final Supplier<IStoryFunction> CREATE_ARRAY = REGISTRY.register("create_array", ArrayFunctions::create);
    public static final Supplier<IStoryFunction> IS_ARRAY = REGISTRY.register("is_array", ArrayFunctions::isArray);
    public static final Supplier<IStoryFunction> ARRAY_SIZE = REGISTRY.register("array_size", ArrayFunctions::size);
    public static final Supplier<IStoryFunction> ARRAY_SET = REGISTRY.register("array_set", ArrayFunctions::set);
    public static final Supplier<IStoryFunction> ARRAY_GET = REGISTRY.register("array_get", ArrayFunctions::get);
    public static final Supplier<IStoryFunction> ARRAY_ADD = REGISTRY.register("array_add", ArrayFunctions::add);
    public static final Supplier<IStoryFunction> ARRAY_REMOVE = REGISTRY.register("array_remove", ArrayFunctions::remove);
    public static final Supplier<IStoryFunction> ARRAY_CONTAINS = REGISTRY.register("array_contains", ArrayFunctions::contains);

    public static final Supplier<IStoryFunction> CREATE_MAP = REGISTRY.register("create_map", MapFunctions::create);
    public static final Supplier<IStoryFunction> IS_MAP = REGISTRY.register("is_map", MapFunctions::isMap);
    public static final Supplier<IStoryFunction> MAP_SIZE = REGISTRY.register("map_size", MapFunctions::size);
    public static final Supplier<IStoryFunction> MAP_SET = REGISTRY.register("map_set", MapFunctions::set);
    public static final Supplier<IStoryFunction> MAP_GET = REGISTRY.register("map_get", MapFunctions::get);
    public static final Supplier<IStoryFunction> MAP_REMOVE = REGISTRY.register("map_remove", MapFunctions::remove);
    public static final Supplier<IStoryFunction> MAP_CONTAINS = REGISTRY.register("map_contains", MapFunctions::contains);

    // </editor-fold>

    // <editor-fold desc="Game functions.">

    public static final Supplier<IStoryFunction> GET_PLAYER_NAME = REGISTRY.register("get_player_name", GetPlayerNameFunction::new);
    public static final Supplier<IStoryFunction> GET_WORLD_DAY_TIME = REGISTRY.register("get_world_day_time", () -> new WorldTimeFunction("getWorldDayTime", level -> (int)(level.getDefaultClockTime() % 24000L)));
    public static final Supplier<IStoryFunction> GET_WORLD_GAME_TIME = REGISTRY.register("get_world_game_time", () -> new WorldTimeFunction("getWorldGameTime", level -> (int)(level.getGameTime() % Integer.MAX_VALUE)));
    public static final Supplier<IStoryFunction> GET_WORLD_DAY = REGISTRY.register("get_world_day", () -> new WorldTimeFunction("getWorldDay", level -> (int)(level.getDefaultClockTime() / 24000L % Integer.MAX_VALUE)));
    public static final Supplier<IStoryFunction> GET_REAL_TIME = REGISTRY.register("get_real_time", RealTimeFunction::new);

    public static final Supplier<IStoryFunction> RUN_COMMAND = REGISTRY.register("run_command", () -> new RunCommandFunction("runCommand", ServerPlayer::createCommandSourceStack));
    public static final Supplier<IStoryFunction> RUN_UNLIMITED_COMMAND = REGISTRY.register("run_unlimited_command", () -> new RunCommandFunction("runUnlimitedCommand", player -> player.createCommandSourceStack()
            .withPermission(LevelBasedPermissionSet.OWNER)));
    public static final Supplier<IStoryFunction> RUN_SILENT_UNLIMITED_COMMAND = REGISTRY.register("run_silent_unlimited_command", () -> new RunCommandFunction("runSilentUnlimitedCommand", player -> player.createCommandSourceStack()
            .withPermission(LevelBasedPermissionSet.OWNER)
            .withSuppressedOutput()));
    public static final Supplier<IStoryFunction> RUN_SERVER_COMMAND = REGISTRY.register("run_server_command", () -> new RunCommandFunction("runServerCommand", player -> {
        var server = player.level().getServer();
        return server.createCommandSourceStack()
                .withEntity(player)
                .withPosition(player.position())
                .withRotation(player.getRotationVector())
                .withLevel(player.level());
    }));

    public static final Supplier<IStoryFunction> GET_SCOREBOARD = REGISTRY.register("get_scoreboard", ScoreboardFunction::getScoreBoard);
    public static final Supplier<IStoryFunction> SET_SCOREBOARD = REGISTRY.register("set_scoreboard", ScoreboardValuedFunction::setScoreBoard);
    public static final Supplier<IStoryFunction> ADD_SCOREBOARD = REGISTRY.register("add_scoreboard", ScoreboardValuedFunction::addScoreBoard);
    public static final Supplier<IStoryFunction> SUB_SCOREBOARD = REGISTRY.register("sub_scoreboard", ScoreboardValuedFunction::subScoreBoard);
    public static final Supplier<IStoryFunction> MULTIPLY_SCOREBOARD = REGISTRY.register("multiply_scoreboard", ScoreboardValuedFunction::multiplyScoreBoard);

    public static final Supplier<IStoryFunction> GET_STORAGE = REGISTRY.register("get_storage", GetStorageFunction::new);
    public static final Supplier<IStoryFunction> SET_STORAGE = REGISTRY.register("set_storage", SetStorageFunction::new);

    public static final Supplier<IStoryFunction> HAS_ITEM = REGISTRY.register("has_item", HasItemFunction::new);
    public static final Supplier<IStoryFunction> COUNT_ITEM = REGISTRY.register("count_item", CountItemFunction::new);
    public static final Supplier<IStoryFunction> GIVE_ITEM = REGISTRY.register("give_item", GiveItemFunction::new);
    public static final Supplier<IStoryFunction> TAKE_ITEM = REGISTRY.register("take_item", TakeItemFunction::new);

    // </editor-fold>
}
