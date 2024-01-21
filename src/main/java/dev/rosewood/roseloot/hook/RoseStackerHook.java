package dev.rosewood.roseloot.hook;

import dev.rosewood.rosestacker.api.RoseStackerAPI;
import dev.rosewood.rosestacker.stack.StackedEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

public class RoseStackerHook {

    private static Boolean enabled;

    public static boolean isEnabled() {
        if (enabled != null)
            return enabled;
        return enabled = Bukkit.getPluginManager().getPlugin("RoseStacker") != null;
    }

    public static boolean isEntireEntityStackDying(LivingEntity entity) {
        if (!isEnabled())
            return false;

        StackedEntity stackedEntity = RoseStackerAPI.getInstance().getStackedEntity(entity);
        if (stackedEntity == null || stackedEntity.getStackSize() <= 1)
            return false;

        return stackedEntity.isEntireStackKilledOnDeath();
    }

}
