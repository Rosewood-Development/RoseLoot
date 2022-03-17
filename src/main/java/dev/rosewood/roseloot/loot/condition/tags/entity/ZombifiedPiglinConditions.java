package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import org.bukkit.entity.PigZombie;

public class ZombifiedPiglinConditions extends EntityConditions {

    public ZombifiedPiglinConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("zombified-piglin-angry", context -> context.getAs(LootContextParams.LOOTED_ENTITY, PigZombie.class).filter(PigZombie::isAngry).isPresent());
    }

}
