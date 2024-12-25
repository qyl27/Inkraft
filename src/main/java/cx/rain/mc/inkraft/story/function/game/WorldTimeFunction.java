package cx.rain.mc.inkraft.story.function.game;

import cx.rain.mc.inkraft.story.IStoryVariable;
import cx.rain.mc.inkraft.story.StoryInstance;
import cx.rain.mc.inkraft.story.function.IStoryFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
    public IStoryVariable<?> apply(StoryInstance instance, String... args) {
        var id = args[0];
        var level = tryParseLevel(instance, id);
        var result = function.apply(level);
        return new IStoryVariable.Int(result);
    }

    private static Level tryParseLevel(StoryInstance instance, String id) {
        try {
            if (id.isEmpty()) {
                var levelId = ResourceLocation.tryParse(id);
                if (levelId != null) {
                    return instance.getPlayer().server.getLevel(ResourceKey.create(Registries.DIMENSION, levelId));
                }
            }
        } catch (Exception ignored) {
        }

        return instance.getPlayer().serverLevel();
    }
}
