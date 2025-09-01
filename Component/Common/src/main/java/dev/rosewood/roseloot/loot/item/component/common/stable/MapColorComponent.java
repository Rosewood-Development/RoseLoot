package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapItemColor;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class MapColorComponent implements LootItemComponent {

    private final Color color;

    public MapColorComponent(ConfigurationSection section) {
        if (section.isString("map-color")) {
            String color = section.getString("map-color");
            Color mapColor = null;
            if (color != null) {
                try {
                    mapColor = Color.fromRGB(java.awt.Color.decode(color).getRGB());
                } catch (NumberFormatException ignored) { }
            }
            this.color = mapColor;
        } else {
            ConfigurationSection mapColorSection = section.getConfigurationSection("map-color");
            if (mapColorSection != null) {
                int red = mapColorSection.getInt("red", 0);
                int green = mapColorSection.getInt("green", 0);
                int blue = mapColorSection.getInt("blue", 0);
                this.color = Color.fromRGB(red, green, blue);
            } else {
                this.color = null;
            }
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.color != null) {
            itemStack.setData(DataComponentTypes.MAP_COLOR, MapItemColor.mapItemColor().color(this.color).build());
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.MAP_COLOR))
            return;

        MapItemColor mapColor = itemStack.getData(DataComponentTypes.MAP_COLOR);
        stringBuilder.append("map-color: '#").append(String.format("%06x", mapColor.color().asRGB())).append("'\n");
    }

} 
