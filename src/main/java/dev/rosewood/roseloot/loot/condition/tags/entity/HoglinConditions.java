package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import org.bukkit.entity.Hoglin;

public class HoglinConditions extends EntityConditions {

    public HoglinConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("hoglin-unhuntable", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Hoglin.class).filter(x -> !x.isAbleToBeHunted()).isPresent());
    }

}
