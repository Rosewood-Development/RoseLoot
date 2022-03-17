package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.EnchantingUtils;
import java.util.Optional;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * enchantment:sharpness,3
 * value 1: The enchantment
 * value 2: The minimum level of the enchantment
 */
public class EnchantmentCondition extends LootCondition {

    private Enchantment enchantment;
    private int level;

    public EnchantmentCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        Optional<ItemStack> item = context.getItemUsed();
        if (!item.isPresent())
            return false;

        ItemMeta meta = item.get().getItemMeta();
        if (meta == null)
            return false;

        return meta.getEnchantLevel(this.enchantment) >= this.level;
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length == 0)
            return false;

        this.enchantment = EnchantingUtils.getEnchantmentByName(values[0]);

        if (values.length > 1) {
            try {
                this.level = Integer.parseInt(values[1]);
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            this.level = 1;
        }

        return this.enchantment != null;
    }

}
