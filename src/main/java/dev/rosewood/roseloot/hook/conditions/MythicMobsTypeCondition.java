package dev.rosewood.roseloot.hook.conditions;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class MythicMobsTypeCondition extends LootCondition {

    private static Object apiHelper;
    private static Method method_BukkitAPIHelper_getMythicMobInstance;
    private static Method method_ActiveMob_getMobType;
    static {
        // MythicMobs pre 5.0.1 support
        try {
            Class<?> class_MythicMobs = Class.forName("io.lumine.xikage.mythicmobs.MythicMobs");
            Method method_MythicMobs_inst = class_MythicMobs.getDeclaredMethod("inst");
            Method method_MythicMobs_getAPIHelper = class_MythicMobs.getDeclaredMethod("getAPIHelper");
            Class<?> class_BukkitAPIHelper = Class.forName("io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper");
            method_BukkitAPIHelper_getMythicMobInstance = class_BukkitAPIHelper.getDeclaredMethod("getMythicMobInstance", Entity.class);
            Class<?> class_ActiveMob = Class.forName("io.lumine.xikage.mythicmobs.mobs.ActiveMob");
            method_ActiveMob_getMobType = class_ActiveMob.getDeclaredMethod("getMobType");
            apiHelper = method_MythicMobs_getAPIHelper.invoke(method_MythicMobs_inst.invoke(null));
        } catch (ReflectiveOperationException ignored) { }
    }

    private List<String> types;

    public MythicMobsTypeCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        Optional<LivingEntity> lootedEntity = context.get(LootContextParams.LOOTED_ENTITY);
        if (lootedEntity.isEmpty())
            return false;

        LivingEntity entity = lootedEntity.get();
        if (apiHelper != null) {
            try {
                Object activeMob = method_BukkitAPIHelper_getMythicMobInstance.invoke(apiHelper, entity);
                if (activeMob == null)
                    return false;

                return this.types.contains((String) method_ActiveMob_getMobType.invoke(activeMob));
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
                return false;
            }
        }

        ActiveMob activeMob = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity);
        if (activeMob == null)
            return false;

        return this.types.contains(activeMob.getMobType());
    }

    @Override
    public boolean parseValues(String[] values) {
        this.types = new ArrayList<>(List.of(values));
        return !this.types.isEmpty();
    }

}
