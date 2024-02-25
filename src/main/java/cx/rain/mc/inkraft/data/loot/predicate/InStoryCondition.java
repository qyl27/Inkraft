package cx.rain.mc.inkraft.data.loot.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cx.rain.mc.inkraft.InkraftPlatform;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public class InStoryCondition implements LootItemCondition {
    public static final Codec<InStoryCondition> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.BOOL.fieldOf("isInStory").forGetter(InStoryCondition::isInStory))
                    .apply(instance, InStoryCondition::new)
    );

    public static final LootItemConditionType CONDITION_TYPE = new LootItemConditionType(CODEC);

    private boolean isInStory = false;

    public InStoryCondition(boolean isInStory) {
        this.isInStory = isInStory;
    }

    public boolean isInStory() {
        return isInStory;
    }

    @Override
    public @NotNull LootItemConditionType getType() {
        return CONDITION_TYPE;
    }

    @Override
    public boolean test(LootContext lootContext) {
        var entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (entity instanceof ServerPlayer player) {
            return InkraftPlatform.getPlayerStoryStateHolder(player).isInStory() == isInStory;
        }

        return false;
    }
}
