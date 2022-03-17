package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import org.bukkit.entity.Strider;

public class StriderConditions extends EntityConditions {

    public StriderConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("strider-shivering", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Strider.class).filter(Strider::isShivering).isPresent());
    }

}
