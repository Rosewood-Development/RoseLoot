package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import org.bukkit.entity.Strider;

public class StriderConditions extends EntityConditions {

    public StriderConditions() {
        LootConditions.registerTag("strider-shivering", context -> context.getLootedEntity() instanceof Strider && ((Strider) context.getLootedEntity()).isShivering());
    }

}
