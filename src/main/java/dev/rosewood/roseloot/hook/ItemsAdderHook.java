package dev.rosewood.roseloot.hook;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class ItemsAdderHook {

    private static Boolean enabled;

    public static boolean isEnabled() {
        if (enabled != null)
            return enabled;
        return enabled = Bukkit.getPluginManager().getPlugin("ItemsAdder") != null;
    }

    public static int getDamage(ItemStack itemStack, Damageable damageable) {
        if (!isEnabled())
            return damageable.getDamage();

        CustomStack customStack = CustomStack.byItemStack(itemStack);
        if (customStack == null || !customStack.hasCustomDurability())
            return damageable.getDamage();

        return customStack.getMaxDurability() - customStack.getDurability();
    }

}
