package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class VehicleCondition extends BaseLootCondition {

    private List<EntityType> vehicleTypes;

    public VehicleCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        return context.get(LootContextParams.LOOTED_ENTITY)
                .map(LivingEntity::getVehicle)
                .map(Entity::getType)
                .filter(this.vehicleTypes::contains)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        this.vehicleTypes = new ArrayList<>();

        for (String value : values) {
            try {
                if (value.startsWith("#")) {
                    Set<EntityType> tagEntities = LootUtils.getTagValues(value.substring(1), EntityType.class, "entity_types");
                    if (tagEntities != null) {
                        this.vehicleTypes.addAll(tagEntities);
                        continue;
                    }
                }

                this.vehicleTypes.add(EntityType.valueOf(value.toUpperCase()));
            } catch (Exception ignored) { }
        }

        return !this.vehicleTypes.isEmpty();
    }

}
