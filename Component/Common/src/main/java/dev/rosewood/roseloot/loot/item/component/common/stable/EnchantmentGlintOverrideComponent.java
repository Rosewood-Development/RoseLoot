package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class EnchantmentGlintOverrideComponent implements LootItemComponent {

    private final boolean value;

    public EnchantmentGlintOverrideComponent(ConfigurationSection section) {
        this.value = section.getBoolean("enchantment-glint-override");
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        itemStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, this.value);
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE))
            return;

        stringBuilder.append("enchantment-glint-override: ").append(itemStack.getData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE)).append('\n');
    }

}
