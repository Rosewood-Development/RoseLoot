package dev.rosewood.roseloot.loot.item.component.v1_21_3;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapItemColor;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class MapColorComponent implements LootItemComponent {

    private final Color color;

    public MapColorComponent(ConfigurationSection section) {
        ConfigurationSection mapColorSection = section.getConfigurationSection("map-color");
        if (mapColorSection != null) {
            Color color = null;
            if (mapColorSection.contains("color")) {
                try {
                    color = Color.fromRGB(java.awt.Color.decode(mapColorSection.getString("color")).getRGB());
                } catch (NumberFormatException ignored) { }
            }
            
            if (color == null) {
                int red = mapColorSection.getInt("red", 0);
                int green = mapColorSection.getInt("green", 0);
                int blue = mapColorSection.getInt("blue", 0);
                color = Color.fromRGB(red, green, blue);
            }
            
            this.color = color;
        } else {
            this.color = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.color != null) {
            itemStack.setData(DataComponentTypes.MAP_COLOR, MapItemColor.mapItemColor().color(this.color).build());
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.MAP_COLOR))
            return;

        MapItemColor mapColor = itemStack.getData(DataComponentTypes.MAP_COLOR);
        stringBuilder.append("map-color:\n");
        stringBuilder.append("  color: '#").append(String.format("%06x", mapColor.color().asRGB())).append("'\n");
    }

} 
