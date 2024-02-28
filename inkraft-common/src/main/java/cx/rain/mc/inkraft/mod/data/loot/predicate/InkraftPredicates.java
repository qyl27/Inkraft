package cx.rain.mc.inkraft.mod.data.loot.predicate;

import cx.rain.mc.inkraft.mod.InkraftMod;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class InkraftPredicates {
    public static final DeferredRegister<LootItemConditionType> CONDITIONS = DeferredRegister.create(InkraftMod.ID, Registries.LOOT_CONDITION_TYPE);

    public static void register() {
        CONDITIONS.register();
    }

    public static final RegistrySupplier<LootItemConditionType> IN_STORY = CONDITIONS.register("in_story", () -> InStoryCondition.CONDITION_TYPE);
}
