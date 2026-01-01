package dev.rosewood.roseloot.loot.item.meta;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.StructureType;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

public class MapItemLootMeta extends ItemLootMeta {

    private StructureType destination;
    private MapView.Scale scale;
    private Integer searchRadius;
    private Boolean skipKnownStructures;

    public MapItemLootMeta(ConfigurationSection section) {
        super(section);

        String destinationString = section.getString("destination");
        if (destinationString != null) {
            for (StructureType value : StructureType.getStructureTypes().values()) {
                if (value.getName().equalsIgnoreCase(destinationString)) {
                    this.destination = value;
                    break;
                }
            }
        }

        String scaleString = section.getString("scale");
        if (scaleString != null) {
            for (MapView.Scale value : MapView.Scale.values()) {
                if (value.name().equalsIgnoreCase(scaleString)) {
                    this.scale = value;
                    break;
                }
            }
        }

        if (section.isInt("search-radius")) this.searchRadius = section.getInt("search-radius");
        if (section.isBoolean("skip-known-strutures")) this.skipKnownStructures = section.getBoolean("skip-known-structures");
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext context) {
        // Just keep this as a default map if a destination is not specified
        if (this.destination == null) {
            itemStack = super.apply(itemStack, context);
            return itemStack;
        }

        Optional<Location> origin = context.get(LootContextParams.ORIGIN);
        if (origin.isEmpty())
            return itemStack;

        World world = origin.get().getWorld();
        if (world == null) {
            itemStack = super.apply(itemStack, context);
            return itemStack;
        }

        MapView.Scale scale = this.scale != null ? this.scale : MapView.Scale.NORMAL;
        int searchRadius = this.searchRadius != null ? this.searchRadius : 50;
        boolean skipKnownStructures = this.skipKnownStructures != null ? this.skipKnownStructures : true;

        ItemStack explorerMap = null;
        try {
            explorerMap = Bukkit.createExplorerMap(world, origin.get(), this.destination, searchRadius, skipKnownStructures);
            if (itemStack.getItemMeta() instanceof MapMeta itemMeta) {
                MapView mapView = itemMeta.getMapView();
                if (mapView != null) {
                    mapView.setScale(scale);
                    explorerMap.setItemMeta(itemMeta);
                }
            }
        } catch (Exception e) {
            RoseLoot.getInstance().getLogger().warning("Failed to apply map item loot meta to item stack. Likely unable to find structure.");
        }

        if (explorerMap == null)
            explorerMap = new ItemStack(Material.MAP);

        return super.apply(explorerMap, context);
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        // Nothing to see here
    }

}
