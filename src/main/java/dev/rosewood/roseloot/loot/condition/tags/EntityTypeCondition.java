package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class EntityTypeCondition extends LootCondition {

    private List<EntityType> entityTypes;

    public EntityTypeCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        LivingEntity entity = context.getLootedEntity();
        if (entity == null)
            return false;

        return this.entityTypes.contains(entity.getType());
    }

    @Override
    public boolean parseValues(String[] values) {
        this.entityTypes = new ArrayList<>();

        for (String value : values) {
            try {
                if (value.startsWith("#")) {
                    Set<EntityType> tagEntities = LootUtils.getTaggedEntities(value.substring(1));
                    if (tagEntities != null) {
                        this.entityTypes.addAll(tagEntities);
                        continue;
                    }
                }

                this.entityTypes.add(EntityType.valueOf(value.toUpperCase()));
            } catch (Exception ignored) { }
        }

        return !this.entityTypes.isEmpty();
    }

}
