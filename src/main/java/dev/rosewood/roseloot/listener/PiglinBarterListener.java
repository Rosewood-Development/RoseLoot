package dev.rosewood.roseloot.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.listener.helper.LazyLootTableListener;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.OverwriteExisting;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.ConfigurationManager.Setting;
import dev.rosewood.roseloot.util.EntitySpawnUtil;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
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

        this.piglinBarterItems = Setting.PIGLIN_BARTER_ITEMS.getStringList().stream()
                .map(Material::matchMaterial)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Bukkit.getWorlds().stream()
                .flatMap(x -> x.getEntitiesByClass(Piglin.class).stream())
                .forEach(this::setPiglinBarterItems);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPiglinBarter(PiglinBarterEvent event) {
        Piglin piglin = event.getEntity();
        if (Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(piglin.getWorld().getName())))
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
        LootResult lootResult = LOOT_TABLE_MANAGER.getLoot(LootTableTypes.PIGLIN_BARTER, lootContext);
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
        if (Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(piglin.getWorld().getName())))
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
