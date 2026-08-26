package cx.rain.mc.inkraft.story.function.game.player;

import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.value.StringStoryValue;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;

import java.util.Optional;
import java.util.function.Function;

public final class PlayerStatFunctions implements IStoryFunction {
    private final String name;
    private final Function<ResolvedStat, IStoryValue<?, ?>> resultFactory;

    private PlayerStatFunctions(String name, Function<ResolvedStat, IStoryValue<?, ?>> resultFactory) {
        this.name = name;
        this.resultFactory = resultFactory;
    }

    public static PlayerStatFunctions value() {
        return new PlayerStatFunctions("getPlayerStat", stat -> new IntStoryValue(stat.value()));
    }

    public static PlayerStatFunctions formatted() {
        return new PlayerStatFunctions("getFormattedPlayerStat",
                stat -> new StringStoryValue(stat.stat().format(stat.value())));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.requireCount(args, 2);
        FunctionArgs.requireTyped(args, 0, String.class);
        FunctionArgs.requireTyped(args, 1, String.class);

        var typeId = Identifier.tryParse(FunctionArgs.getString(args[0]));
        var valueId = Identifier.tryParse(FunctionArgs.getString(args[1]));
        if (typeId == null || valueId == null) {
            return BoolStoryValue.FALSE;
        }

        var type = BuiltInRegistries.STAT_TYPE.getValue(typeId);
        if (type == null) {
            return BoolStoryValue.FALSE;
        }

        return resolve(instance.getPlayer(), type, valueId)
                .map(resultFactory)
                .orElse(BoolStoryValue.FALSE);
    }

    private static <T> Optional<ResolvedStat> resolve(ServerPlayer player, StatType<T> type, Identifier valueId) {
        var registry = type.getRegistry();
        if (!registry.containsKey(valueId)) {
            return Optional.empty();
        }

        var statValue = registry.getValue(valueId);
        if (statValue == null) {
            return Optional.empty();
        }

        var stat = type.get(statValue);
        return Optional.of(new ResolvedStat(stat, player.getStats().getValue(stat)));
    }

    private record ResolvedStat(Stat<?> stat, int value) {
    }
}
