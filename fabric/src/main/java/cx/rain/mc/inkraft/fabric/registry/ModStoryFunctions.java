package cx.rain.mc.inkraft.fabric.registry;

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
import cx.rain.mc.inkraft.story.function.system.line.IsEndedFunction;
import cx.rain.mc.inkraft.story.function.system.line.PauseFunction;
import cx.rain.mc.inkraft.story.function.system.line.SetLineTicksFunction;
import cx.rain.mc.inkraft.story.function.system.line.UnsetLineTicksFunction;
import cx.rain.mc.inkraft.story.function.system.parse.ParseBoolFunction;
import cx.rain.mc.inkraft.story.function.system.parse.ParseFloatFunction;
import cx.rain.mc.inkraft.story.function.system.parse.ParseIntFunction;
import cx.rain.mc.inkraft.story.function.system.parse.ToStringFunction;
import cx.rain.mc.inkraft.story.function.system.variable.*;
import cx.rain.mc.inkraft.utility.IdHelper;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class ModStoryFunctions {

    private static void register(MappedRegistry<IStoryFunction> registry, String id, Supplier<IStoryFunction> storyFunction) {
        registry.register(ResourceKey.create(InkraftRegistries.STORY_FUNCTIONS, IdHelper.modLoc(id)), storyFunction.get(), RegistrationInfo.BUILT_IN);
    }

    public static void register() {
        var registry = FabricRegistryBuilder.create(InkraftRegistries.STORY_FUNCTIONS)
                .attribute(RegistryAttribute.OPTIONAL)
                .buildAndRegister();
        // <editor-fold desc="System functions.">

        register(registry, "is_debug", IsDebugFunction::new);

        register(registry, "new_flow", NewFlowFunction::new);
        register(registry, "remove_flow", RemoveFlowFunction::new);
        register(registry, "flow_to", FlowToFunction::new);
        register(registry, "flow_to_default", FlowToDefaultFunction::new);
        register(registry, "is_in_flow", IsInFlowFunction::new);
        register(registry, "is_in_default_flow", IsInDefaultFlowFunction::new);

        register(registry, "is_ended", IsEndedFunction::new);
        register(registry, "pause", PauseFunction::new);
        register(registry, "set_line_ticks", SetLineTicksFunction::new);
        register(registry, "unset_line_ticks", UnsetLineTicksFunction::new);

        register(registry, "has_variable", HasVariableFunction::new);
        register(registry, "get_variable", GetVariableFunction::new);
        register(registry, "set_variable", SetVariableFunction::new);
        register(registry, "unset_variable", UnsetVariableFunction::new);
        register(registry, "clear_variable", ClearVariableFunction::new);

        register(registry, "log_debug", () -> new LogFunction("logDebug", Logger::debug));
        register(registry, "log_info", () -> new LogFunction("logInfo", Logger::info));
        register(registry, "log_warn", () -> new LogFunction("logWarn", Logger::warn));
        register(registry, "log_error", () -> new LogFunction("logError", Logger::error));

        register(registry, "parse_bool", ParseBoolFunction::new);
        register(registry, "parse_int", ParseIntFunction::new);
        register(registry, "parse_float", ParseFloatFunction::new);
        register(registry, "to_string", ToStringFunction::new);

        register(registry, "create_array", ArrayFunctions::create);
        register(registry, "is_array", ArrayFunctions::isArray);
        register(registry, "array_size", ArrayFunctions::size);
        register(registry, "array_set", ArrayFunctions::set);
        register(registry, "array_get", ArrayFunctions::get);
        register(registry, "array_add", ArrayFunctions::add);
        register(registry, "array_remove", ArrayFunctions::remove);
        register(registry, "array_contains", ArrayFunctions::contains);

        register(registry, "create_map", MapFunctions::create);
        register(registry, "is_map", MapFunctions::isMap);
        register(registry, "map_size", MapFunctions::size);
        register(registry, "map_set", MapFunctions::set);
        register(registry, "map_get", MapFunctions::get);
        register(registry, "map_remove", MapFunctions::remove);
        register(registry, "map_contains", MapFunctions::contains);

        // </editor-fold>

        // <editor-fold desc="Game functions.">

        register(registry, "get_player_name", GetPlayerNameFunction::new);
        register(registry, "get_world_day_time", () -> new WorldTimeFunction("getWorldDayTime", level -> (int)(level.getDefaultClockTime() % 24000L)));
        register(registry, "get_world_game_time", () -> new WorldTimeFunction("getWorldGameTime", level -> (int)(level.getGameTime() % Integer.MAX_VALUE)));
        register(registry, "get_world_day", () -> new WorldTimeFunction("getWorldDay", level -> (int)(level.getDefaultClockTime() / 24000L % Integer.MAX_VALUE)));
        register(registry, "get_real_time", RealTimeFunction::new);

        register(registry, "run_command", () -> new RunCommandFunction("runCommand", ServerPlayer::createCommandSourceStack));
        register(registry, "run_unlimited_command", () -> new RunCommandFunction("runUnlimitedCommand", player -> player.createCommandSourceStack()
                .withPermission(LevelBasedPermissionSet.OWNER)));
        register(registry, "run_silent_unlimited_command", () -> new RunCommandFunction("runSilentUnlimitedCommand", player -> player.createCommandSourceStack()
                .withPermission(LevelBasedPermissionSet.OWNER)
                .withSuppressedOutput()));
        register(registry, "run_server_command", () -> new RunCommandFunction("runServerCommand", player -> {
            var server = player.level().getServer();
            return server.createCommandSourceStack()
                    .withEntity(player)
                    .withPosition(player.position())
                    .withRotation(player.getRotationVector())
                    .withLevel(player.level());
        }));

        register(registry, "get_scoreboard", ScoreboardFunction::getScoreBoard);
        register(registry, "set_scoreboard", ScoreboardValuedFunction::setScoreBoard);
        register(registry, "add_scoreboard", ScoreboardValuedFunction::addScoreBoard);
        register(registry, "sub_scoreboard", ScoreboardValuedFunction::subScoreBoard);
        register(registry, "multiply_scoreboard", ScoreboardValuedFunction::multiplyScoreBoard);

        register(registry, "get_storage", GetStorageFunction::new);
        register(registry, "set_storage", SetStorageFunction::new);

        register(registry, "has_item", HasItemFunction::new);
        register(registry, "count_item", CountItemFunction::new);
        register(registry, "give_item", GiveItemFunction::new);
        register(registry, "take_item", TakeItemFunction::new);

        // </editor-fold>
    }
}
