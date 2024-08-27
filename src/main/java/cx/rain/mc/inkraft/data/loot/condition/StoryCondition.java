package cx.rain.mc.inkraft.data.loot.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cx.rain.mc.inkraft.Inkraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public record StoryCondition(ResourceLocation storyId) implements LootItemCondition {
    public static final ResourceLocation ANY = ResourceLocation.fromNamespaceAndPath(Inkraft.MODID, "any");

    public static final Codec<StoryCondition> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(ResourceLocation.CODEC.optionalFieldOf("story", ANY).forGetter(StoryCondition::storyId))
                    .apply(instance, StoryCondition::new)
    );
    public static final MapCodec<StoryCondition> MAP_CODEC = MapCodec.assumeMapUnsafe(CODEC);
    public static final LootItemConditionType TYPE = new LootItemConditionType(MAP_CODEC);

    @Override
    public @NotNull LootItemConditionType getType() {
        return TYPE;
    }

    @Override
    public boolean test(LootContext lootContext) {
        var entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (entity instanceof ServerPlayer player) {
            var instance = Inkraft.getInstance().getStoriesManager().get(player);
            if (ANY.equals(storyId)) {
                return !instance.isStoryEnded();
            } else {
                return storyId.equals(instance.getData().getStory()) && !instance.isStoryEnded();
            }
        }
        return false;
    }
}
