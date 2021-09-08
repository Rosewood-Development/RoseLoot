package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import org.bukkit.entity.Bee;

public class BeeConditions extends EntityConditions {

    public BeeConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("bee-angry", context -> context.getLootedEntity() instanceof Bee && ((Bee) context.getLootedEntity()).getAnger() > 0);
        event.registerLootCondition("bee-has-hive", context -> context.getLootedEntity() instanceof Bee && ((Bee) context.getLootedEntity()).getHive() != null);
        event.registerLootCondition("bee-has-stung", context -> context.getLootedEntity() instanceof Bee && ((Bee) context.getLootedEntity()).hasStung());
        event.registerLootCondition("bee-has-flower", context -> context.getLootedEntity() instanceof Bee && ((Bee) context.getLootedEntity()).getFlower() != null);
        event.registerLootCondition("bee-has-nectar", context -> context.getLootedEntity() instanceof Bee && ((Bee) context.getLootedEntity()).hasNectar());
    }

}
