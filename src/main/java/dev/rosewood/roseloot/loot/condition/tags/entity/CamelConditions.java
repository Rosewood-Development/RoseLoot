package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import org.bukkit.entity.Camel;

public class CamelConditions extends EntityConditions {

    public CamelConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("camel-dashing", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Camel.class).filter(Camel::isDashing).isPresent());
    }

}
