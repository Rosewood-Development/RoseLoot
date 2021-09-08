package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import org.bukkit.entity.Hoglin;

public class HoglinConditions extends EntityConditions {

    public HoglinConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("hoglin-unhuntable", context -> context.getLootedEntity() instanceof Hoglin && !((Hoglin) context.getLootedEntity()).isAbleToBeHunted());
    }

}
