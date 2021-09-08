package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import org.bukkit.entity.Vex;

public class VexConditions extends EntityConditions {

    public VexConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("vex-charging", context -> context.getLootedEntity() instanceof Vex && ((Vex) context.getLootedEntity()).isCharging());
    }

}
