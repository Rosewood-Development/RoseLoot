package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import org.bukkit.entity.PiglinBrute;

public class PiglinBruteConditions extends EntityConditions {

    public PiglinBruteConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("piglin-brute-converting", context -> context.getLootedEntity() instanceof PiglinBrute && ((PiglinBrute) context.getLootedEntity()).isConverting());
        event.registerLootCondition("piglin-brute-immune-to-zombification", context -> context.getLootedEntity() instanceof PiglinBrute && ((PiglinBrute) context.getLootedEntity()).isImmuneToZombification());
    }

}
