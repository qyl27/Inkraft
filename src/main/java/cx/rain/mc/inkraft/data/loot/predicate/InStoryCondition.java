package cx.rain.mc.inkraft.data.loot.predicate;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import cx.rain.mc.inkraft.InkraftPlatform;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class InStoryCondition implements LootItemCondition {
    public static final LootItemConditionType CONDITION_TYPE = new LootItemConditionType(new ConditionSerializer());

    private boolean isInStory = false;

    public InStoryCondition(boolean isInStory) {
        this.isInStory = isInStory;
    }

    @Override
    public LootItemConditionType getType() {
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

    public static class ConditionSerializer implements Serializer<InStoryCondition> {

        @Override
        public void serialize(JsonObject json, InStoryCondition value, JsonSerializationContext serializationContext) {
            json.addProperty("isInStory", value.isInStory);
        }

        @Override
        public InStoryCondition deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            return new InStoryCondition(json.get("isInStory").getAsBoolean());
        }
    }
}
