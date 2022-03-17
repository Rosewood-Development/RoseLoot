package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import org.bukkit.entity.Skeleton;

public class SkeletonConditions extends EntityConditions {

    public SkeletonConditions(LootConditionRegistrationEvent event) {
        if (NMSUtil.getVersionNumber() >= 17)
            event.registerLootCondition("skeleton-converting", context -> context.getAs(LootContextParams.LOOTED_ENTITY, Skeleton.class).filter(Skeleton::isConverting).isPresent());
    }

}
