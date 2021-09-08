package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import org.bukkit.entity.Snowman;

public class SnowmanConditions extends EntityConditions {

    public SnowmanConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("snowman-no-pumpkin", context -> context.getLootedEntity() instanceof Snowman && ((Snowman) context.getLootedEntity()).isDerp());
    }

}
