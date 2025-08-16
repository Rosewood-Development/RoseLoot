package dev.rosewood.roseloot.loot.item.component.v1_21_3;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Enchantable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class EnchantableComponent implements LootItemComponent {

    private final NumberProvider value;

    public EnchantableComponent(ConfigurationSection section) {
        ConfigurationSection enchantableSection = section.getConfigurationSection("enchantable");
        if (enchantableSection != null) {
            this.value = NumberProvider.fromSection(enchantableSection, "value", null);
        } else {
            this.value = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.value != null) {
            int enchantValue = this.value.getInteger(context);
            if (enchantValue > 0)
                itemStack.setData(DataComponentTypes.ENCHANTABLE, Enchantable.enchantable(enchantValue));
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.ENCHANTABLE))
            return;

        Enchantable enchantable = itemStack.getData(DataComponentTypes.ENCHANTABLE);
        stringBuilder.append("enchantable:\n");
        stringBuilder.append("  value: ").append(enchantable.value()).append('\n');
    }

} 
