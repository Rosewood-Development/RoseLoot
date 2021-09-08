package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import org.bukkit.entity.Skeleton;

public class SkeletonConditions extends EntityConditions {

    public SkeletonConditions(LootConditionRegistrationEvent event) {
        if (NMSUtil.getVersionNumber() >= 17)
            event.registerLootCondition("skeleton-converting", context -> context.getLootedEntity() instanceof Skeleton && ((Skeleton) context.getLootedEntity()).isConverting());
    }

}
