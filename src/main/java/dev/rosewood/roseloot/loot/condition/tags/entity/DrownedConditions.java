package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import org.bukkit.entity.Drowned;

public class DrownedConditions extends EntityConditions {

    public DrownedConditions() {
        LootConditions.registerTag("drowned-converting", context -> context.getLootedEntity() instanceof Drowned && ((Drowned) context.getLootedEntity()).isConverting());
    }

}
