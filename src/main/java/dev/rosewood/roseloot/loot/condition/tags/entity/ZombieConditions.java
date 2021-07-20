package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import org.bukkit.entity.Zombie;

public class ZombieConditions extends EntityConditions {

    public ZombieConditions() {
        LootConditions.registerTag("zombie-converting", context -> context.getLootedEntity() instanceof Zombie && ((Zombie) context.getLootedEntity()).isConverting());
    }

}
