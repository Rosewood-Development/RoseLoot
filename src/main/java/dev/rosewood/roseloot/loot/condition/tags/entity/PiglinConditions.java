package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import org.bukkit.entity.Piglin;

public class PiglinConditions extends EntityConditions {

    public PiglinConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("piglin-converting", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Piglin.class).filter(Piglin::isConverting).isPresent());
        event.registerLootCondition("piglin-immune-to-zombification", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Piglin.class).filter(Piglin::isImmuneToZombification).isPresent());
        event.registerLootCondition("piglin-unable-to-hunt", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Piglin.class).filter(x -> !x.isAbleToHunt()).isPresent());
    }

}
