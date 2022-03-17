package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import org.bukkit.entity.IronGolem;

public class IronGolemConditions extends EntityConditions {

    public IronGolemConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("iron-golem-player-created", context -> context.getAs(LootContextParams.LOOTED_ENTITY, IronGolem.class).filter(IronGolem::isPlayerCreated).isPresent());
    }

}
