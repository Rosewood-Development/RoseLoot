package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import org.bukkit.entity.Strider;

public class StriderConditions extends EntityConditions {

    public StriderConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("strider-shivering", context -> context.getLootedEntity() instanceof Strider && ((Strider) context.getLootedEntity()).isShivering());
    }

}
