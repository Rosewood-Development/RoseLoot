package dev.rosewood.roseloot.loot.item.component.latest;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class RepairCostComponent implements LootItemComponent {

    private final NumberProvider value;

    public RepairCostComponent(ConfigurationSection section) {
        this.value = NumberProvider.fromSection(section, "repair-cost", -1);
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        int value = this.value.getInteger(context);
        if (value > 0)
            itemStack.setData(DataComponentTypes.REPAIR_COST, value);
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.REPAIR_COST))
            return;

        stringBuilder.append("repair-cost: ").append(itemStack.getData(DataComponentTypes.REPAIR_COST)).append('\n');
    }

}
