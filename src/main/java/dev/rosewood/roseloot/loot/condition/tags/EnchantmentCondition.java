package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantmentCondition extends LootCondition {

    private Enchantment enchantment;
    private int level;

    public EnchantmentCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        LivingEntity looter = context.getLooter();
        if (looter == null)
            return false;

        EntityEquipment equipment = looter.getEquipment();
        if (equipment == null)
            return false;

        ItemStack item = equipment.getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return false;

        return meta.getEnchantLevel(this.enchantment) >= this.level;
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length == 0)
            return false;

        this.enchantment = Enchantment.getByKey(NamespacedKey.minecraft(values[0].toLowerCase()));

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
