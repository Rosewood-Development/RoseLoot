package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.item.meta.ItemLootMeta;
import dev.rosewood.roseloot.util.EnchantingUtils;
import dev.rosewood.roseloot.util.NumberProvider;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemLootItem implements LootItem<List<ItemStack>> {

    private final Material item;
    private final NumberProvider amount;
    private final NumberProvider maxAmount;
    private final ItemLootMeta itemLootMeta;
    private final EnchantmentBonus enchantmentBonus;

    public ItemLootItem(Material item, NumberProvider amount, NumberProvider maxAmount, ItemLootMeta itemLootMeta, EnchantmentBonus enchantmentBonus) {
        this.item = item;
        this.amount = amount;
        this.maxAmount = maxAmount;
        this.itemLootMeta = itemLootMeta;
        this.enchantmentBonus = enchantmentBonus;
    }

    protected ItemLootItem() {
        this(null, null, null, null, null);
    }

    @Override
    public List<ItemStack> create(LootContext context) {
        List<ItemStack> generatedItems = new ArrayList<>();

        int amount = this.amount.getInteger();
        ItemStack itemUsed = context.getItemUsed();
        if (this.enchantmentBonus != null && itemUsed != null) {
            ItemMeta itemMeta = itemUsed.getItemMeta();
            if (itemMeta != null) {
                int level = Math.min(itemMeta.getEnchantLevel(this.enchantmentBonus.getEnchantment()), this.enchantmentBonus.getMaxBonusLevels().getInteger());
                for (int i = 0; i < level; i++)
                    amount += this.enchantmentBonus.getBonusPerLevel().getInteger();
            }
        }

        amount = Math.min(amount, this.maxAmount.getInteger());

        if (amount > 0) {
            int maxStackSize = this.item.getMaxStackSize();
            ItemStack itemStack = this.itemLootMeta.apply(new ItemStack(this.item), context);
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

        return generatedItems;
    }

    public static ItemLootItem fromSection(ConfigurationSection section) {
        String itemString = section.getString("item");
        if (itemString == null)
            return null;

        Material item = Material.matchMaterial(itemString);
        if (item == null)
            return null;

        NumberProvider amount = NumberProvider.fromSection(section, "amount", 1);
        NumberProvider maxAmount = NumberProvider.fromSection(section, "max-amount", Integer.MAX_VALUE);
        ConfigurationSection enchantmentBonusSection = section.getConfigurationSection("enchantment-bonus");
        ItemLootItem.EnchantmentBonus enchantmentBonus = null;
        if (enchantmentBonusSection != null) {
            String enchantmentString = enchantmentBonusSection.getString("enchantment");
            if (enchantmentString != null) {
                Enchantment enchantment = EnchantingUtils.getEnchantmentByName(enchantmentString);
                NumberProvider bonusPerLevel = NumberProvider.fromSection(enchantmentBonusSection, "bonus-per-level", 0);
                NumberProvider maxBonusLevels = NumberProvider.fromSection(enchantmentBonusSection, "max-bonus-levels", Integer.MAX_VALUE);
                if (enchantment != null)
                    enchantmentBonus = new ItemLootItem.EnchantmentBonus(enchantment, bonusPerLevel, maxBonusLevels);
            }
        }

        ItemLootMeta itemLootMeta = ItemLootMeta.fromSection(item, section);
        return new ItemLootItem(item, amount, maxAmount, itemLootMeta, enchantmentBonus);
    }

    public static String toSection(ItemStack itemStack) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("item: ").append(itemStack.getType().name().toLowerCase()).append('\n');
        stringBuilder.append("amount: ").append(itemStack.getAmount()).append('\n');

        ItemLootMeta.applyProperties(itemStack, stringBuilder);

        return stringBuilder.toString();
    }

    public static class EnchantmentBonus {

        private final Enchantment enchantment;
        private final NumberProvider bonusPerLevel;
        private final NumberProvider maxBonusLevels;

        public EnchantmentBonus(Enchantment enchantment, NumberProvider bonusPerLevel, NumberProvider maxBonusLevels) {
            this.enchantment = enchantment;
            this.bonusPerLevel = bonusPerLevel;
            this.maxBonusLevels = maxBonusLevels;
        }

        public Enchantment getEnchantment() {
            return this.enchantment;
        }

        public NumberProvider getBonusPerLevel() {
            return this.bonusPerLevel;
        }

        public NumberProvider getMaxBonusLevels() {
            return this.maxBonusLevels;
        }

    }

}
