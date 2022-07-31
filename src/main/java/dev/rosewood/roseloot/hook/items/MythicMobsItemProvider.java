package dev.rosewood.roseloot.hook.items;

import dev.rosewood.roseloot.loot.context.LootContext;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.inventory.ItemStack;

public class MythicMobsItemProvider extends ItemProvider {

    public MythicMobsItemProvider() {
        super("MythicMobs");
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
        if (!this.isEnabled())
            return null;

        return MythicBukkit.inst().getItemManager().getItemStack(id, 1);
    }

    @Override
    public String getItemId(ItemStack item) {
        if (!this.isEnabled())
            return null;

        return MythicBukkit.inst().getItemManager().getMythicTypeFromItem(item);
    }

    @Override
    public String getConditionSuffix() {
        return "-item";
    }

}
