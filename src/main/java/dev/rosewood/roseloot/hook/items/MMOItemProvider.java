package dev.rosewood.roseloot.hook.items;

import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class MMOItemProvider implements ItemProvider {

    private final boolean enabled;

    public MMOItemProvider() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("MMOItems");
    }

    @Override
    public ItemStack getItem(String id) {
        if (!this.enabled)
            return null;

        String[] parts = id.split(":", 2);
        if (parts.length != 2)
            return null;

        return MMOItems.plugin.getItem(parts[0], parts[1]);
    }

}
