package dev.rosewood.roseloot.hook;

import dev.rosewood.rosestacker.api.RoseStackerAPI;
import dev.rosewood.rosestacker.stack.StackedEntity;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDeathEvent;

public class RoseStackerHook {

    private static Boolean enabled;

    public static boolean isEnabled() {
        if (enabled != null)
            return enabled;
        return enabled = Bukkit.getPluginManager().getPlugin("RoseStacker") != null;
    }

    public static boolean shouldIgnoreNormalDeathEvent(EntityDeathEvent event) {
        if (!isEnabled())
            return false;

        RoseStackerAPI api = RoseStackerAPI.getInstance();
        StackedEntity stackedEntity = api.getStackedEntity(event.getEntity());
        if (stackedEntity == null)
            return false;

        return stackedEntity.areMultipleEntitiesDying(event);
    }

}
