package dev.rosewood.roseloot.hook;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderHook {

    private static Boolean enabled;

    public static boolean isEnabled() {
        if (enabled != null)
            return enabled;
        return enabled = Bukkit.getPluginManager().getPlugin("ItemsAdder") != null;
    }

    public static boolean offsetItemDurability(ItemStack itemStack, int amount) {
        if (!isEnabled())
            return false;

        CustomStack customStack = CustomStack.byItemStack(itemStack);
        if (customStack == null || !customStack.hasCustomDurability())
            return false;

        customStack.setDurability(customStack.getDurability() + amount);
        return true;
    }

}
