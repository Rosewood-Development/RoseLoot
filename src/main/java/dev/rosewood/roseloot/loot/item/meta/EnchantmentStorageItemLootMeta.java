package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.LootContext;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class EnchantmentStorageItemLootMeta extends ItemLootMeta {

    public EnchantmentStorageItemLootMeta(ConfigurationSection section) {
        super(section);
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        super.apply(itemStack, context);

        EnchantmentStorageMeta itemMeta = (EnchantmentStorageMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        if (this.enchantments != null) {
            for (EnchantmentData enchantmentData : this.enchantments) {
                int level = enchantmentData.getLevel().getInteger();
                if (level > 0)
                    itemMeta.addStoredEnchant(enchantmentData.getEnchantment(), level, true);
            }
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

}
