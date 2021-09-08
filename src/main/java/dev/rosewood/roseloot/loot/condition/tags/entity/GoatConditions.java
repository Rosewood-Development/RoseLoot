package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import org.bukkit.entity.Goat;

public class GoatConditions extends EntityConditions {

    public GoatConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("goat-screaming", context -> context.getLootedEntity() instanceof Goat && ((Goat) context.getLootedEntity()).isScreaming());
    }

}
