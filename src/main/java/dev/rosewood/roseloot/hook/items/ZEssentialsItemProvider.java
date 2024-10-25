package dev.rosewood.roseloot.hook.items;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import fr.maxlego08.essentials.api.EssentialsPlugin;
import fr.maxlego08.essentials.module.modules.ItemModule;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class ZEssentialsItemProvider extends ItemProvider {

    public ZEssentialsItemProvider() {
        super("zEssentials", false);
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
        EssentialsPlugin essentialsPlugin = (EssentialsPlugin) Bukkit.getPluginManager().getPlugin("zEssentials");
        if (essentialsPlugin == null)
            return null;

        ItemModule itemModule = essentialsPlugin.getModuleManager().getModule(ItemModule.class);
        if (itemModule == null) {
            RoseLoot.getInstance().getLogger().warning("zEssentials ItemModule is not enabled");
            return null;
        }

        return context.getLootingPlayer()
                .map(player -> itemModule.getItemStack(id, player))
                .orElse(null);
    }

}
