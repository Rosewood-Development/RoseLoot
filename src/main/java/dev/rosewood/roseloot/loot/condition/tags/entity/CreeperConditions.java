package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import org.bukkit.entity.Creeper;

public class CreeperConditions extends EntityConditions {

    public CreeperConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("creeper-charged", context -> context.getLootedEntity() instanceof Creeper && ((Creeper) context.getLootedEntity()).isPowered());
    }

}
