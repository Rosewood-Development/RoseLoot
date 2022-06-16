package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.EnchantingUtils;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * enchantment-chance-table:looting,5%,6.25%,8.33%,10%
 * value 1: The enchantment required
 * value 2+: The chance of passing, indexed by the enchantment level
 *
 * An enchantment level greater than the max index will use the max index instead
 */
public class EnchantmentChanceTableCondition extends LootCondition {

    private Enchantment enchantment;
    private List<Double> chances;

    public EnchantmentChanceTableCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        Optional<ItemStack> item = context.getItemUsed();
        if (item.isEmpty())
            return LootUtils.checkChance(this.chances.get(0));

        ItemMeta meta = item.get().getItemMeta();
        if (meta == null)
            return LootUtils.checkChance(this.chances.get(0));

        int chanceIndex = Math.min(meta.getEnchantLevel(this.enchantment), this.chances.size() - 1);
        double chance = this.chances.get(chanceIndex);

        return LootUtils.checkChance(chance);
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length < 2)
            return false;

        this.chances = new ArrayList<>();

        try {
            this.enchantment = EnchantingUtils.getEnchantmentByName(values[0]);

            for (int i = 1; i < values.length; i++) {
                String value = values[i];
                if (value.endsWith("%"))
                    value = value.substring(0, value.length() - 1);
                this.chances.add(Double.parseDouble(value) / 100);
            }

            return !this.chances.isEmpty();
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
