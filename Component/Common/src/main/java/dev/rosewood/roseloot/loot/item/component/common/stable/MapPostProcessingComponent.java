package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.item.MapPostProcessing;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class MapPostProcessingComponent implements LootItemComponent {

    private final MapPostProcessing postProcessing;

    public MapPostProcessingComponent(ConfigurationSection section) {
        String value = section.getString("map-post-processing");
        this.postProcessing = value != null ? parsePostProcessing(value) : null;
    }

    private static MapPostProcessing parsePostProcessing(String value) {
        try {
            return MapPostProcessing.valueOf(value);
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
        if (!itemStack.isDataOverridden(DataComponentTypes.MAP_POST_PROCESSING))
            return;

        MapPostProcessing postProcessing = itemStack.getData(DataComponentTypes.MAP_POST_PROCESSING);
        stringBuilder.append("map-post-processing: ").append(postProcessing.name().toLowerCase()).append('\n');
    }

} 
