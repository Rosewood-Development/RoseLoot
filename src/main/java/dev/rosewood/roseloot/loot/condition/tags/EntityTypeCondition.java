package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
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
        return context.get(LootContextParams.LOOTED_ENTITY)
                .map(LivingEntity::getType)
                .filter(this.entityTypes::contains)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        this.entityTypes = new ArrayList<>();

        for (String value : values) {
            try {
                if (value.startsWith("#")) {
                    Set<EntityType> tagEntities = LootUtils.getTags(value.substring(1), EntityType.class, "entity_types");
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
