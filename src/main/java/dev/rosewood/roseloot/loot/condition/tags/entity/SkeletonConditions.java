package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import org.bukkit.entity.Skeleton;

public class SkeletonConditions extends EntityConditions {

    public SkeletonConditions() {
        if (NMSUtil.getVersionNumber() >= 17)
            LootConditions.registerTag("skeleton-converting", context -> context.getLootedEntity() instanceof Skeleton && ((Skeleton) context.getLootedEntity()).isConverting());
    }

}
