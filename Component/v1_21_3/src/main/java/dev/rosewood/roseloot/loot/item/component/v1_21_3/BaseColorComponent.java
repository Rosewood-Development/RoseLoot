package dev.rosewood.roseloot.loot.item.component.v1_21_3;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class BaseColorComponent implements LootItemComponent {

    private final DyeColor color;

    public BaseColorComponent(ConfigurationSection section) {
        String colorName = section.getString("base-color");
        if (colorName != null) {
            DyeColor dyeColor;
            try {
                dyeColor = DyeColor.valueOf(colorName.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                dyeColor = null;
            }
            this.color = dyeColor;
        } else {
            this.color = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.color != null)
            itemStack.setData(DataComponentTypes.BASE_COLOR, this.color);
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.BASE_COLOR))
            return;

        DyeColor color = itemStack.getData(DataComponentTypes.BASE_COLOR);
        stringBuilder.append("base-color: ").append(color.name().toLowerCase()).append('\n');
    }

}
