package dev.rosewood.roseloot.loot.item.component.latest;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapDecorations;
import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCursor;

class MapDecorationsComponent implements LootItemComponent {

    private final Map<String, DecorationData> decorations;

    public MapDecorationsComponent(ConfigurationSection section) {
        ConfigurationSection mapDecorationsSection = section.getConfigurationSection("map-decorations");
        if (mapDecorationsSection != null) {
            this.decorations = new HashMap<>();
            for (String key : mapDecorationsSection.getKeys(false)) {
                ConfigurationSection decorationSection = mapDecorationsSection.getConfigurationSection(key);
                if (decorationSection != null) {
                    MapCursor.Type type = Registry.MAP_DECORATION_TYPE.get(Key.key(decorationSection.getString("type", "red_marker").toLowerCase()));
                    NumberProvider x = NumberProvider.fromSection(decorationSection, "x", 0);
                    NumberProvider z = NumberProvider.fromSection(decorationSection, "z", 0);
                    NumberProvider rotation = NumberProvider.fromSection(decorationSection, "rotation", 0);
                    this.decorations.put(key, new DecorationData(type, x, z, rotation));
                }
            }
        } else {
            this.decorations = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.decorations != null && !this.decorations.isEmpty()) {
            Map<String, MapDecorations.DecorationEntry> entries = new HashMap<>();
            for (Map.Entry<String, DecorationData> entry : this.decorations.entrySet()) {
                DecorationData data = entry.getValue();
                entries.put(entry.getKey(), MapDecorations.decorationEntry(
                    data.type,
                    data.x.getDouble(context),
                    data.z.getDouble(context),
                    (float) data.rotation.getDouble(context)
                ));
            }
            itemStack.setData(DataComponentTypes.MAP_DECORATIONS, MapDecorations.mapDecorations(entries));
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.MAP_DECORATIONS))
            return;

        MapDecorations mapDecorations = itemStack.getData(DataComponentTypes.MAP_DECORATIONS);
        stringBuilder.append("map-decorations:\n");
        
        for (Map.Entry<String, MapDecorations.DecorationEntry> entry : mapDecorations.decorations().entrySet()) {
            MapDecorations.DecorationEntry decoration = entry.getValue();
            NamespacedKey decorationKey = Registry.MAP_DECORATION_TYPE.getKey(decoration.type());
            if (decorationKey != null) {
                stringBuilder.append("  ").append(entry.getKey()).append(":\n");
                stringBuilder.append("    type: ").append(decorationKey.asMinimalString()).append("\n");
                stringBuilder.append("    x: ").append(decoration.x()).append("\n");
                stringBuilder.append("    z: ").append(decoration.z()).append("\n");
                stringBuilder.append("    rotation: ").append(decoration.rotation()).append("\n");
            }
        }
    }

    private record DecorationData(
        MapCursor.Type type,
        NumberProvider x,
        NumberProvider z,
        NumberProvider rotation
    ) {}

} 
