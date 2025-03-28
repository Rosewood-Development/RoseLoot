package dev.rosewood.roseloot.loot.item.component.latest;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

class LodestoneTrackerComponent implements LootItemComponent {

    private final NumberProvider x;
    private final NumberProvider y;
    private final NumberProvider z;
    private final String world;
    private final boolean tracked;

    public LodestoneTrackerComponent(ConfigurationSection section) {
        ConfigurationSection trackerSection = section.getConfigurationSection("lodestone-tracker");
        if (trackerSection != null) {
            ConfigurationSection locationSection = trackerSection.getConfigurationSection("location");
            if (locationSection != null) {
                this.x = NumberProvider.fromSection(locationSection, "x", null);
                this.y = NumberProvider.fromSection(locationSection, "y", null);
                this.z = NumberProvider.fromSection(locationSection, "z", null);
                this.world = locationSection.getString("world");
            } else {
                this.x = null;
                this.y = null;
                this.z = null;
                this.world = null;
            }
            this.tracked = trackerSection.getBoolean("tracked", true);
        } else {
            this.x = null;
            this.y = null;
            this.z = null;
            this.world = null;
            this.tracked = true;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.x != null && this.y != null && this.z != null && this.world != null) {
            World world = Bukkit.getWorld(this.world);
            if (world != null) {
                Location location = new Location(
                    world,
                    this.x.getDouble(context),
                    this.y.getDouble(context),
                    this.z.getDouble(context)
                );
                itemStack.setData(DataComponentTypes.LODESTONE_TRACKER, LodestoneTracker.lodestoneTracker(location, this.tracked));
            }
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.LODESTONE_TRACKER))
            return;

        LodestoneTracker tracker = itemStack.getData(DataComponentTypes.LODESTONE_TRACKER);
        Location location = tracker.location();
        
        stringBuilder.append("lodestone-tracker:\n");
        if (location != null) {
            stringBuilder.append("  location:\n");
            stringBuilder.append("    world: '").append(location.getWorld().getName()).append("'\n");
            stringBuilder.append("    x: ").append(location.getX()).append('\n');
            stringBuilder.append("    y: ").append(location.getY()).append('\n');
            stringBuilder.append("    z: ").append(location.getZ()).append('\n');
        }
        stringBuilder.append("  tracked: ").append(tracker.tracked()).append('\n');
    }
} 
