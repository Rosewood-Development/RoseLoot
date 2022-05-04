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
        return enabled = Bukkit.getPluginManager().isPluginEnabled("RoseStacker");
    }

    public static boolean useCustomEntityDeathHandling() {
        return isEnabled() && RoseStackerAPI.getInstance().isEntityStackMultipleDeathEventCalled();
    }

    public static boolean isEntityStacked(LivingEntity entity) {
        if (!isEnabled())
            return false;

        StackedEntity stackedEntity = RoseStackerAPI.getInstance().getStackedEntity(entity);
        return stackedEntity != null && stackedEntity.getStackSize() > 1;
    }

}
