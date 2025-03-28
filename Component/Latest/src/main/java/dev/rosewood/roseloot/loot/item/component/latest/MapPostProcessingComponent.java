package dev.rosewood.roseloot.loot.item.component.latest;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.item.MapPostProcessing;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class MapPostProcessingComponent implements LootItemComponent {

    private final MapPostProcessing postProcessing;

    public MapPostProcessingComponent(ConfigurationSection section) {
        ConfigurationSection mapPostProcessingSection = section.getConfigurationSection("map-post-processing");
        this.postProcessing = mapPostProcessingSection != null ? parsePostProcessing(mapPostProcessingSection) : null;
    }

    private static MapPostProcessing parsePostProcessing(ConfigurationSection section) {
        try {
            return MapPostProcessing.valueOf(section.getString("type", "LOCK").toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.postProcessing != null)
            itemStack.setData(DataComponentTypes.MAP_POST_PROCESSING, this.postProcessing);
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.MAP_POST_PROCESSING))
            return;

        MapPostProcessing postProcessing = itemStack.getData(DataComponentTypes.MAP_POST_PROCESSING);
        stringBuilder.append("map-post-processing:\n");
        stringBuilder.append("  type: ").append(postProcessing.name().toLowerCase()).append("\n");
    }

} 
