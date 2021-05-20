package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.util.LootUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * enchantment-chance:10%,looting,3%
 * value 1: Base percentage
 * value 2: The enchantment
 * value 3: Extra chance to add for each level of the enchantment
 */
public class EnchantmentChanceCondition extends LootCondition {

    private double chance;
    private Enchantment enchantment;
    private double chancePerLevel;

    public EnchantmentChanceCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        ItemStack item = context.getItemUsed();
        if (item == null)
            return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return false;

        return LootUtils.checkChance(this.chance + this.chancePerLevel * meta.getEnchantLevel(this.enchantment));
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length != 3)
            return false;

        try {
            String value1 = values[0];
            if (value1.endsWith("%"))
                value1 = value1.substring(0, value1.length() - 1);

            String value2 = values[2];
            if (value2.endsWith("%"))
                value2 = value2.substring(0, value2.length() - 1);

            this.chance = Double.parseDouble(value1) / 100;
            this.enchantment = Enchantment.getByKey(NamespacedKey.minecraft(values[1].toLowerCase()));
            this.chancePerLevel = Double.parseDouble(value2) / 100;
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
