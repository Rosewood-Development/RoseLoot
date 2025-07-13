package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class LooterVehicleCondition extends BaseLootCondition {

    private List<EntityType> vehicleTypes;

    public LooterVehicleCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        Optional<Entity> looterOptional = context.get(LootContextParams.LOOTER);
        if (looterOptional.isEmpty())
            return false;

        Entity entity = LootUtils.propagateKiller(looterOptional.get());
        if (!(entity instanceof LivingEntity livingEntity))
            return false;

        Entity vehicle = entity.getVehicle();
        if (vehicle == null)
            return false;

        return this.vehicleTypes.contains(vehicle.getType());
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
