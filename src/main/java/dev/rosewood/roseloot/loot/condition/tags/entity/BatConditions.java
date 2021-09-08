package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import org.bukkit.entity.Bat;

public class BatConditions extends EntityConditions {

    public BatConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("bat-sleeping", context -> context.getLootedEntity() instanceof Bat && !((Bat) context.getLootedEntity()).isAwake());
    }

}
