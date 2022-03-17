package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import org.bukkit.entity.Goat;

public class GoatConditions extends EntityConditions {

    public GoatConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("goat-screaming", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Goat.class).filter(Goat::isScreaming).isPresent());
    }

}
