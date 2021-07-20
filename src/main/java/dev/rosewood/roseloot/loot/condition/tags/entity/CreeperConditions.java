package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import org.bukkit.entity.Creeper;

public class CreeperConditions extends EntityConditions {

    public CreeperConditions() {
        LootConditions.registerTag("creeper-charged", context -> context.getLootedEntity() instanceof Creeper && ((Creeper) context.getLootedEntity()).isPowered());
    }

}
