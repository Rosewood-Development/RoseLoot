package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import org.bukkit.entity.Vex;

public class VexConditions extends EntityConditions {

    public VexConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("vex-charging", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Vex.class).filter(Vex::isCharging).isPresent());
    }

}
