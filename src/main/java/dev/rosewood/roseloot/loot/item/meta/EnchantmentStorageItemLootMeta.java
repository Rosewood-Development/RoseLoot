package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.LootContext;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
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

        if (this.enchantments != null)
            for (Map.Entry<Enchantment, Integer> entry : this.enchantments.entrySet())
                itemMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

}
