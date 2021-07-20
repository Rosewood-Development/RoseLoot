package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import org.bukkit.entity.Goat;

public class GoatConditions extends EntityConditions {

    public GoatConditions() {
        LootConditions.registerTag("goat-screaming", context -> context.getLootedEntity() instanceof Goat && ((Goat) context.getLootedEntity()).isScreaming());
    }

}
