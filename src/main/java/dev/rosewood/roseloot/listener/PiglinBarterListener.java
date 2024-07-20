package dev.rosewood.roseloot.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.EntitySpawnUtil;
import dev.rosewood.roseloot.config.SettingKey;
import dev.rosewood.roseloot.listener.helper.LazyLootTableListener;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.OverwriteExisting;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.LootTableManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Piglin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.inventory.ItemStack;

public class PiglinBarterListener extends LazyLootTableListener {

    private Set<Material> piglinBarterItems;

    public PiglinBarterListener(RosePlugin rosePlugin) {
        super(rosePlugin, LootTableTypes.PIGLIN_BARTER);
    }

    @Override
    public void enable() {
        super.enable();

        this.piglinBarterItems = new HashSet<>(this.rosePlugin.getRoseConfig().get(SettingKey.PIGLIN_BARTER_ITEMS));

        Bukkit.getWorlds().stream()
                .flatMap(x -> x.getEntitiesByClass(Piglin.class).stream())
                .forEach(this::setPiglinBarterItems);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPiglinBarter(PiglinBarterEvent event) {
        Piglin piglin = event.getEntity();
        if (this.rosePlugin.getRoseConfig().get(SettingKey.DISABLED_WORLDS).stream().anyMatch(x -> x.equalsIgnoreCase(piglin.getWorld().getName())))
            return;

        ItemStack inputItem = event.getInput();
        if (!this.piglinBarterItems.contains(inputItem.getType())) {
            event.setCancelled(true);
            return;
        }

        LootContext lootContext = LootContext.builder()
                .put(LootContextParams.ORIGIN, piglin.getLocation())
                .put(LootContextParams.LOOTED_ENTITY, piglin)
                .put(LootContextParams.INPUT_ITEM, inputItem)
                .put(LootContextParams.HAS_EXISTING_ITEMS, !event.getOutcome().isEmpty())
                .build();
        LootResult lootResult = this.rosePlugin.getManager(LootTableManager.class).getLoot(LootTableTypes.PIGLIN_BARTER, lootContext);
        if (lootResult.isEmpty())
            return;

        LootContents lootContents = lootResult.getLootContents();

        // Overwrite existing loot if applicable
        List<ItemStack> outputItems = event.getOutcome();
        if (lootResult.doesOverwriteExisting(OverwriteExisting.ITEMS))
            outputItems.clear();

        // Set items and drop experience
        outputItems.addAll(lootResult.getLootContents().getItems());

        int experience = lootContents.getExperience();
        if (experience > 0)
            EntitySpawnUtil.spawn(piglin.getLocation(), ExperienceOrb.class, x -> x.setExperience(experience));

        lootContents.triggerExtras(piglin.getLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPiglinSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() != EntityType.PIGLIN)
            return;

        Piglin piglin = (Piglin) event.getEntity();
        if (this.rosePlugin.getRoseConfig().get(SettingKey.DISABLED_WORLDS).stream().anyMatch(x -> x.equalsIgnoreCase(piglin.getWorld().getName())))
            return;

        this.setPiglinBarterItems(piglin);
    }

    private void setPiglinBarterItems(Piglin piglin) {
        piglin.getBarterList().forEach(piglin::removeBarterMaterial);
        this.piglinBarterItems.forEach(material -> {
            piglin.addBarterMaterial(material);
            piglin.addMaterialOfInterest(material);
        });
    }

}
