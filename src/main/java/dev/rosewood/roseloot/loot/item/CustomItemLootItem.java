package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.hook.items.CustomItemPlugin;
import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.Optional;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class CustomItemLootItem extends ItemLootItem {

    private final CustomItemPlugin customItemPlugin;

    protected CustomItemLootItem(CustomItemPlugin customItemPlugin, ItemLootItem base) {
        super(base);
        this.customItemPlugin = customItemPlugin;
        this.resolveItem(LootContext.none());
    }

    @Override
    protected Optional<ItemStack> resolveItem(LootContext context) {
        String itemId = this.item.get(context);
        ItemStack itemStack = this.customItemPlugin.resolveItem(context, itemId);
        if (itemStack == null)
            this.logFailToResolveMessage(itemId);
        return Optional.ofNullable(itemStack);
    }

    @Override
    protected String getFailToResolveMessage(String itemId) {
        return super.getFailToResolveMessage(itemId) + " from [" + this.customItemPlugin.name().toLowerCase() + "]";
    }

    public static CustomItemLootItem fromSection(ConfigurationSection section) {
        String plugin = section.getString("plugin");
        CustomItemPlugin customItemPlugin = CustomItemPlugin.fromString(plugin);
        if (customItemPlugin == null)
            return null;

        ItemLootItem base = ItemLootItem.fromSection(section, "item", false);
        if (base == null)
            return null;

        return new CustomItemLootItem(customItemPlugin, base);
    }

}
