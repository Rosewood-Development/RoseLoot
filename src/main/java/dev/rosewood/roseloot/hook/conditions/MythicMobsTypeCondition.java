package dev.rosewood.roseloot.hook.conditions;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.entity.LivingEntity;

public class MythicMobsTypeCondition extends BaseLootCondition {

    private List<String> types;

    public MythicMobsTypeCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        Optional<LivingEntity> lootedEntity = context.get(LootContextParams.LOOTED_ENTITY);
        if (lootedEntity.isEmpty())
            return false;

        ActiveMob activeMob = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(lootedEntity.get());
        if (activeMob == null)
            return false;

        context.getPlaceholders().add("mythic_mob_level", activeMob.getLevel());
        return this.types.contains(activeMob.getMobType());
    }

    @Override
    public boolean parseValues(String[] values) {
        this.types = new ArrayList<>(List.of(values));
        return !this.types.isEmpty();
    }

}
