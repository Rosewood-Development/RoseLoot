package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class SpawnReasonCondition extends LootCondition {

    private List<SpawnReason> spawnReasons;

    public SpawnReasonCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        LivingEntity looted = context.getLootedEntity();
        if (looted == null)
            return false;

        return this.spawnReasons.contains(LootUtils.getEntitySpawnReason(looted));
    }

    @Override
    public boolean parseValues(String[] values) {
        this.spawnReasons = new ArrayList<>();

        try {
            for (String value : values)
                this.spawnReasons.add(SpawnReason.valueOf(value.toUpperCase()));
        } catch (Exception ex) {
            return false;
        }

        return !this.spawnReasons.isEmpty();
    }

}
