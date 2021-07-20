package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import org.bukkit.entity.PigZombie;

public class ZombifiedPiglinConditions extends EntityConditions {

    public ZombifiedPiglinConditions() {
        LootConditions.registerTag("zombified-piglin-angry", context -> context.getLootedEntity() instanceof PigZombie && ((PigZombie) context.getLootedEntity()).isAngry());
    }

}
