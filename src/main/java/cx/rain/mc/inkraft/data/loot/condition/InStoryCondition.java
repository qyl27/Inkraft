package cx.rain.mc.inkraft.data.loot.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cx.rain.mc.inkraft.Inkraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public record InStoryCondition(boolean isInStory) implements LootItemCondition {
    public static final Codec<InStoryCondition> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.BOOL.fieldOf("isInStory").forGetter(InStoryCondition::isInStory))
                    .apply(instance, InStoryCondition::new)
    );
    public static final MapCodec<InStoryCondition> MAP_CODEC = MapCodec.assumeMapUnsafe(CODEC);
    public static final LootItemConditionType TYPE = new LootItemConditionType(MAP_CODEC);

    @Override
    public @NotNull LootItemConditionType getType() {
        return TYPE;
    }

    @Override
    public boolean test(LootContext lootContext) {
        var entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (entity instanceof ServerPlayer player) {
            return Inkraft.getInstance().getStoriesManager().get(player).isStoryEnded() == isInStory;
        }
        return false;
    }
}
