package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import org.bukkit.entity.PigZombie;

public class ZombifiedPiglinConditions extends EntityConditions {

    public ZombifiedPiglinConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("zombified-piglin-angry", context -> context.getLootedEntity() instanceof PigZombie && ((PigZombie) context.getLootedEntity()).isAngry());
    }

}
