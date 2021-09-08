package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import org.bukkit.entity.Piglin;

public class PiglinConditions extends EntityConditions {

    public PiglinConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("piglin-converting", context -> context.getLootedEntity() instanceof Piglin && ((Piglin) context.getLootedEntity()).isConverting());
        event.registerLootCondition("piglin-immune-to-zombification", context -> context.getLootedEntity() instanceof Piglin && ((Piglin) context.getLootedEntity()).isImmuneToZombification());
        event.registerLootCondition("piglin-unable-to-hunt", context -> context.getLootedEntity() instanceof Piglin && !((Piglin) context.getLootedEntity()).isAbleToHunt());
    }

}
