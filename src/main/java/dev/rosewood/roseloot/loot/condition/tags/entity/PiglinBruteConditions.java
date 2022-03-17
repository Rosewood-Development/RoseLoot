package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import org.bukkit.entity.PiglinBrute;

public class PiglinBruteConditions extends EntityConditions {

    public PiglinBruteConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("piglin-brute-converting", context -> context.getAs(LootContextParams.LOOTED_ENTITY, PiglinBrute.class).filter(PiglinBrute::isConverting).isPresent());
        event.registerLootCondition("piglin-brute-immune-to-zombification", context -> context.getAs(LootContextParams.LOOTED_ENTITY, PiglinBrute.class).filter(PiglinBrute::isImmuneToZombification).isPresent());
    }

}
