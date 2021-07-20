package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import org.bukkit.entity.Snowman;

public class SnowmanConditions extends EntityConditions {

    public SnowmanConditions() {
        LootConditions.registerTag("snowman-no-pumpkin", context -> context.getLootedEntity() instanceof Snowman && !((Snowman) context.getLootedEntity()).isDerp());
    }

}
