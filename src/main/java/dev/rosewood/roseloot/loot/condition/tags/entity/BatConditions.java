package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import org.bukkit.entity.Bat;

public class BatConditions extends EntityConditions {

    public BatConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("bat-sleeping", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Bat.class).filter(x -> !x.isAwake()).isPresent());
    }

}
