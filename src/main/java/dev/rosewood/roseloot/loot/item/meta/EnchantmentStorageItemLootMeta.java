package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        itemStack = super.apply(itemStack, context);

        EnchantmentStorageMeta itemMeta = (EnchantmentStorageMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;

        if (this.randomEnchantments != null) {
            List<Enchantment> possibleEnchantments = new ArrayList<>();
            if (!this.randomEnchantments.isEmpty()) {
                // Not empty, use the suggested
                possibleEnchantments.addAll(this.randomEnchantments);
            } else {
                // Empty, pick from every enchantment
                possibleEnchantments.addAll(Arrays.asList(Enchantment.values()));
            }

            Enchantment enchantment = possibleEnchantments.get(LootUtils.RANDOM.nextInt(possibleEnchantments.size()));
            int level = LootUtils.RANDOM.nextInt(enchantment.getMaxLevel()) + 1;
            itemMeta.addStoredEnchant(enchantment, level, true);
        }

        if (this.enchantments != null) {
            for (EnchantmentData enchantmentData : this.enchantments) {
                int level = enchantmentData.level().getInteger(context);
                if (level > 0)
                    itemMeta.addStoredEnchant(enchantmentData.enchantment(), level, true);
            }
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        EnchantmentStorageMeta itemMeta = (EnchantmentStorageMeta) itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        if (!itemMeta.getStoredEnchants().isEmpty()) {
            stringBuilder.append("enchantments:\n");
            for (Map.Entry<Enchantment, Integer> entry : itemMeta.getStoredEnchants().entrySet())
                stringBuilder.append("  ").append(entry.getKey().getKey().getKey()).append(": ").append(entry.getValue());
        }
    }

}
