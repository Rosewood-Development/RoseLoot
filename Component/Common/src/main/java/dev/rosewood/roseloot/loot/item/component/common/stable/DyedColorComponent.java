package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class DyedColorComponent implements LootItemComponent {

    private final Color color;

    public DyedColorComponent(ConfigurationSection section) {
        if (section.isString("dyed-color")) {
            Color color = null;
            try {
                color = Color.fromRGB(java.awt.Color.decode(section.getString("dyed-color")).getRGB());
            } catch (NumberFormatException ignored) { }
            
            this.color = color;
        } else {
            this.color = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        DyedItemColor.Builder dyedItemColor = DyedItemColor.dyedItemColor();

        if (this.color != null)
            dyedItemColor.color(this.color);

        itemStack.setData(DataComponentTypes.DYED_COLOR, dyedItemColor.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.DYED_COLOR))
            return;

        DyedItemColor dyedColor = itemStack.getData(DataComponentTypes.DYED_COLOR);
        stringBuilder.append("dyed-color: '#").append(String.format("%06x", dyedColor.color().asRGB())).append("'\n");
    }

}
