package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import org.bukkit.entity.Creeper;

public class CreeperConditions extends EntityConditions {

    public CreeperConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("creeper-charged", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Creeper.class).filter(Creeper::isPowered).isPresent());
    }

}
