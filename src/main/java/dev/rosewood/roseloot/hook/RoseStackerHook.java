package dev.rosewood.roseloot.hook;

import dev.rosewood.rosestacker.api.RoseStackerAPI;
import dev.rosewood.rosestacker.manager.ConfigurationManager;
import dev.rosewood.rosestacker.stack.StackedEntity;
import dev.rosewood.rosestacker.utils.PersistentDataUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

public class RoseStackerHook {

    private static Boolean enabled;

    public static boolean isEnabled() {
        if (enabled != null)
            return enabled;
        return enabled = Bukkit.getPluginManager().isPluginEnabled("RoseStacker");
    }

    public static boolean useCustomEntityDeathHandling() {
        return isEnabled() && RoseStackerAPI.getInstance().isEntityStackMultipleDeathEventCalled();
    }

    public static boolean isEntireEntityStackDying(LivingEntity entity) {
        if (!isEnabled())
            return false;

        StackedEntity stackedEntity = RoseStackerAPI.getInstance().getStackedEntity(entity);
        if (stackedEntity == null || stackedEntity.getStackSize() > 1)
            return false;

        // This whole section was copy/pasted from RoseStacker
        // This should be changed to use API in the future, but it just doesn't exist right now
        EntityDamageEvent lastDamageCause = entity.getLastDamageCause();
        return stackedEntity.getStackSettings().shouldKillEntireStackOnDeath()
                || (ConfigurationManager.Setting.SPAWNER_DISABLE_MOB_AI_OPTIONS_KILL_ENTIRE_STACK_ON_DEATH.getBoolean() && PersistentDataUtils.isAiDisabled(entity))
                || (lastDamageCause != null && ConfigurationManager.Setting.ENTITY_KILL_ENTIRE_STACK_CONDITIONS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(lastDamageCause.getCause().name())))
                || (entity.getKiller() != null && entity.getKiller().hasPermission("rosestacker.killentirestack"));
    }

}
