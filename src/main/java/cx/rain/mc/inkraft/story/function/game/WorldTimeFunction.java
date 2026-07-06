package cx.rain.mc.inkraft.story.function.game;

import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.FunctionArgs;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public class WorldTimeFunction implements IStoryFunction {
    private final String name;
    private final Function<Level, Integer> function;

    public WorldTimeFunction(String name, Function<Level, Integer> function) {
        this.name = name;
        this.function = function;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IStoryValue<?, ?> apply(StoryInstance instance, IStoryValue<?, ?>... args) {
        FunctionArgs.requireCount(args, 1);
        FunctionArgs.requireTyped(args, 0, String.class);

        var id = FunctionArgs.getString(args[0]);
        var level = tryParseLevel(instance, id);
        var result = function.apply(level);
        return new IntStoryValue(result);
    }

    private static Level tryParseLevel(StoryInstance instance, String id) {
        try {
            if (!id.isEmpty()) {
                var levelId = Identifier.tryParse(id);
                if (levelId != null) {
                    var server = instance.getPlayer().level().getServer();
                    var level = server.getLevel(ResourceKey.create(Registries.DIMENSION, levelId));
                    if (level != null) {
                        return level;
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return instance.getPlayer().level();
    }
}
