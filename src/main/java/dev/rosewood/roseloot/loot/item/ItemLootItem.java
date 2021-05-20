package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.item.meta.ItemLootMeta;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemLootItem extends LootItem {

    private final Material item;
    private final int min;
    private final int max;
    private final ItemLootMeta itemLootMeta;
    private final EnchantmentBonus enchantmentBonus;

    public ItemLootItem(Material item, int min, int max, ItemLootMeta itemLootMeta, EnchantmentBonus enchantmentBonus) {
        this.item = item;
        this.min = min;
        this.max = max;
        this.itemLootMeta = itemLootMeta;
        this.enchantmentBonus = enchantmentBonus;
    }

    @Override
    public LootContents generate(LootContext context) {
        List<ItemStack> generatedItems = new ArrayList<>();

        int max = this.max;
        ItemStack itemUsed = context.getItemUsed();
        if (this.enchantmentBonus != null && itemUsed != null) {
            ItemMeta itemMeta = itemUsed.getItemMeta();
            if (itemMeta != null)
                max += itemMeta.getEnchantLevel(this.enchantmentBonus.getEnchantment()) * this.enchantmentBonus.getBonusPerLevel();
        }

        int amount = LootUtils.randomInRange(this.min, max);
        if (amount > 0) {
            int maxStackSize = this.item.getMaxStackSize();
            ItemStack itemStack = this.itemLootMeta.apply(new ItemStack(this.item));
            while (amount > maxStackSize) {
                amount -= maxStackSize;
                ItemStack clone = itemStack.clone();
                clone.setAmount(maxStackSize);
                generatedItems.add(clone);
            }

            if (amount > 0) {
                ItemStack clone = itemStack.clone();
                clone.setAmount(amount);
                generatedItems.add(clone);
            }
        }

        return new LootContents(generatedItems, Collections.emptyList(), 0);
    }

    public static class EnchantmentBonus {

        private final Enchantment enchantment;
        private final int bonusPerLevel;

        public EnchantmentBonus(Enchantment enchantment, int bonusPerLevel) {
            this.enchantment = enchantment;
            this.bonusPerLevel = bonusPerLevel;
        }

        public Enchantment getEnchantment() {
            return this.enchantment;
        }

        public int getBonusPerLevel() {
            return this.bonusPerLevel;
        }

    }

}
