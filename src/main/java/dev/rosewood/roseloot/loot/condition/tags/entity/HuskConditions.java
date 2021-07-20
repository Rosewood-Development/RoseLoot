package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import org.bukkit.entity.Husk;

public class HuskConditions extends EntityConditions {

    public HuskConditions() {
        LootConditions.registerTag("husk-converting", context -> context.getLootedEntity() instanceof Husk && ((Husk) context.getLootedEntity()).isConverting());
    }

}
