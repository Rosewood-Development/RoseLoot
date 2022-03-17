package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import org.bukkit.entity.Husk;

public class HuskConditions extends EntityConditions {

    public HuskConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("husk-converting", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Husk.class).filter(Husk::isConverting).isPresent());
    }

}
