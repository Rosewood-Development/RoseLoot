package dev.rosewood.roseloot.hook;

import dev.rosewood.rosestacker.api.RoseStackerAPI;
import dev.rosewood.rosestacker.manager.ConfigurationManager;
import dev.rosewood.rosestacker.stack.StackedEntity;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class RoseStackerHook {

    private static Boolean enabled;

    public static boolean isEnabled() {
        if (enabled != null)
            return enabled;
        return enabled = Bukkit.getPluginManager().getPlugin("RoseStacker") != null;
    }

    public static boolean shouldIgnoreNormalDeathEvent(LivingEntity entity) {
        if (!isEnabled())
            return false;

        RoseStackerAPI api = RoseStackerAPI.getInstance();
        StackedEntity stackedEntity = api.getStackedEntity(entity);

        // Don't ignore if single entity kill or not a stack
        if (!api.isEntityStackMultipleDeathEventCalled()
                || stackedEntity == null
                || stackedEntity.getStackSize() == 1)
            return false;

        // Ignore if entire stack kill
        if (stackedEntity.isEntireStackKilledOnDeath())
            return true;

        // Is partial stack kill
        Player killer = entity.getKiller();
        if (!ConfigurationManager.Setting.ENTITY_MULTIKILL_ENABLED.getBoolean())
            return false;

        if (ConfigurationManager.Setting.ENTITY_MULTIKILL_PLAYER_ONLY.getBoolean() && killer == null)
            return false;

        if (!ConfigurationManager.Setting.ENTITY_MULTIKILL_ENCHANTMENT_ENABLED.getBoolean())
            return true;

        if (killer == null)
            return false;

        Enchantment requiredEnchantment = Enchantment.getByKey(NamespacedKey.fromString(ConfigurationManager.Setting.ENTITY_MULTIKILL_ENCHANTMENT_TYPE.getString()));
        if (requiredEnchantment == null)
            return false;

        return killer.getInventory().getItemInMainHand().getEnchantmentLevel(requiredEnchantment) > 0;
    }

}
