package dev.rosewood.roseloot.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.LootTableType;
import dev.rosewood.roseloot.manager.ConfigurationManager;
import dev.rosewood.roseloot.manager.LootTableManager;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Piglin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.inventory.ItemStack;

public class PiglinBarterListener implements Listener {

    private final LootTableManager lootTableManager;

    public PiglinBarterListener(RosePlugin rosePlugin) {
        this.lootTableManager = rosePlugin.getManager(LootTableManager.class);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPiglinBarter(PiglinBarterEvent event) {
        Piglin piglin = event.getEntity();
        if (ConfigurationManager.Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(piglin.getWorld().getName())))
            return;

        LootContext lootContext = new LootContext(piglin, event.getInput());
        LootResult lootResult = this.lootTableManager.getLoot(LootTableType.PIGLIN_BARTER, lootContext);
        LootContents lootContents = lootResult.getLootContents();

        // Overwrite existing loot if applicable
        List<ItemStack> outputItems = event.getOutcome();
        if (lootResult.shouldOverwriteExisting())
            outputItems.clear();

        // Trigger explosion if applicable
        if (lootContents.getExplosionState() != null)
            lootContents.getExplosionState().trigger(piglin.getLocation());

        // Set items and drop experience
        outputItems.addAll(lootResult.getLootContents().getItems());

        int experience = lootContents.getExperience();
        if (experience > 0)
            piglin.getWorld().spawn(piglin.getLocation(), ExperienceOrb.class, x -> x.setExperience(experience));

        // Run commands
        if (!lootContents.getCommands().isEmpty()) {
            Location location = piglin.getLocation();
            StringPlaceholders.Builder stringPlaceholdersBuilder = StringPlaceholders.builder("world", piglin.getWorld().getName())
                    .addPlaceholder("x", location.getX())
                    .addPlaceholder("y", location.getY())
                    .addPlaceholder("z", location.getZ());

            StringPlaceholders stringPlaceholders = stringPlaceholdersBuilder.build();
            for (String command : lootContents.getCommands())
                if (!command.contains("%player%"))
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), stringPlaceholders.apply(command));
        }
    }

}
