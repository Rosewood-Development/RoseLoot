package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapId;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class MapIdComponent implements LootItemComponent {

    private final NumberProvider id;

    public MapIdComponent(ConfigurationSection section) {
        this.id = NumberProvider.fromSection(section, "map-id", null);
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.id != null) {
            itemStack.setData(DataComponentTypes.MAP_ID, MapId.mapId(this.id.getInteger(context)));
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.MAP_ID))
            return;

        MapId mapId = itemStack.getData(DataComponentTypes.MAP_ID);
        stringBuilder.append("map-id: ").append(mapId.id()).append('\n');
    }

} 
