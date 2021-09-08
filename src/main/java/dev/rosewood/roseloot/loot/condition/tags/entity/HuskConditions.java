package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import org.bukkit.entity.Husk;

public class HuskConditions extends EntityConditions {

    public HuskConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("husk-converting", context -> context.getLootedEntity() instanceof Husk && ((Husk) context.getLootedEntity()).isConverting());
    }

}
