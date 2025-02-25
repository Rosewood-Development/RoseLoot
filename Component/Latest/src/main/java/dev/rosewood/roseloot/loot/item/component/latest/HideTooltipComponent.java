package dev.rosewood.roseloot.loot.item.component.latest;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class HideTooltipComponent implements LootItemComponent {

    private final boolean value;

    public HideTooltipComponent(ConfigurationSection section) {
        this.value = section.contains("hide-tooltip");
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.value)
            itemStack.setData(DataComponentTypes.HIDE_TOOLTIP);
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.HIDE_TOOLTIP))
            return;

        stringBuilder.append("hide-tooltip: true\n");
    }

}
