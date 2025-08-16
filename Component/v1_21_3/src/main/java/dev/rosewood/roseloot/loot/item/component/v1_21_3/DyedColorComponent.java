package dev.rosewood.roseloot.loot.item.component.v1_21_3;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class DyedColorComponent implements LootItemComponent {

    private final Color color;
    private final Boolean showInTooltip;

    public DyedColorComponent(ConfigurationSection section) {
        ConfigurationSection dyedColorSection = section.getConfigurationSection("dyed-color");
        if (dyedColorSection != null) {
            Color color = null;
            if (dyedColorSection.contains("color")) {
                try {
                    color = Color.fromRGB(java.awt.Color.decode(dyedColorSection.getString("color")).getRGB());
                } catch (NumberFormatException ignored) { }
            }
            
            this.color = color;
            if (dyedColorSection.isBoolean("show-in-tooltip")) {
                this.showInTooltip = dyedColorSection.getBoolean("show-in-tooltip");
            } else {
                this.showInTooltip = null;
            }
        } else {
            this.color = null;
            this.showInTooltip = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        DyedItemColor.Builder dyedItemColor = DyedItemColor.dyedItemColor();

        if (this.color != null)
            dyedItemColor.color(this.color);

        if (this.showInTooltip != null)
            dyedItemColor.showInTooltip(this.showInTooltip);

        itemStack.setData(DataComponentTypes.DYED_COLOR, dyedItemColor.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.DYED_COLOR))
            return;

        DyedItemColor dyedColor = itemStack.getData(DataComponentTypes.DYED_COLOR);
        stringBuilder.append("dyed-color:\n");
        stringBuilder.append("  color: '#").append(String.format("%06x", dyedColor.color().asRGB())).append("'\n");
        stringBuilder.append("  show-in-tooltip: ").append(dyedColor.showInTooltip()).append('\n');
    }

}
