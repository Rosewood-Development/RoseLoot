package dev.rosewood.roseloot.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.EntitySpawnUtil;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.config.SettingKey;
import dev.rosewood.roseloot.listener.helper.LazyLootTableListener;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.loot.OverwriteExisting;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Vault;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.event.block.VaultDisplayItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class VaultListener extends LazyLootTableListener {

    private final RosePlugin rosePlugin;
    private final Cache<Location, DisplayItemTracker> displayItemTrackers;
    private final List<String> disabledWorlds;

    public VaultListener(RosePlugin rosePlugin) {
        super(rosePlugin, LootTableTypes.VAULT);
        this.rosePlugin = rosePlugin;
        this.displayItemTrackers = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.SECONDS).build();
        this.disabledWorlds = new ArrayList<>();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockDispenseLoot(BlockDispenseLootEvent event) {
        Block block = event.getBlock();
        if (this.disabledWorlds.contains(block.getWorld().getName().toLowerCase()))
            return;

        List<ItemStack> dispensedLoot = event.getDispensedLoot();
        Player player = event.getPlayer();

        NamespacedKey vanillaLootTableKey;
        if (NMSUtil.isPaper()) {
            if (block.getState(false) instanceof Vault vault) {
                vanillaLootTableKey = vault.getLootTable().getKey();
            } else {
                vanillaLootTableKey = null;
            }
        } else {
            if (block.getState() instanceof Vault vault) {
                vanillaLootTableKey = vault.getLootTable().getKey();
            } else {
                vanillaLootTableKey = null;
            }
        }

        LootContext lootContext = LootContext.builder(LootUtils.getEntityLuck(player))
                .put(LootContextParams.ORIGIN, block.getLocation())
                .put(LootContextParams.LOOTED_BLOCK, block)
                .put(LootContextParams.LOOTER, player)
                .put(LootContextParams.HAS_EXISTING_ITEMS, !dispensedLoot.isEmpty())
                .put(LootContextParams.VANILLA_LOOT_TABLE_KEY, vanillaLootTableKey)
                .build();
        LootResult lootResult = this.rosePlugin.getManager(LootTableManager.class).getLoot(LootTableTypes.VAULT, lootContext);
        LootContents lootContents = lootResult.getLootContents();
        List<ItemStack> items = new ArrayList<>(lootContents.getItems());

        if (!lootResult.doesOverwriteExisting(OverwriteExisting.ITEMS))
            items.addAll(dispensedLoot);

        event.setDispensedLoot(items);

        Location dropLocation = block.getLocation();
        int experience = lootContents.getExperience();
        if (experience > 0)
            EntitySpawnUtil.spawn(dropLocation, ExperienceOrb.class, x -> x.setExperience(experience));

        lootContents.triggerExtras(dropLocation);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onVaultDisplayItem(VaultDisplayItemEvent event) {
        Block block = event.getBlock();
        if (this.disabledWorlds.contains(block.getWorld().getName().toLowerCase()))
            return;

        Location location = block.getLocation();
        DisplayItemTracker itemTracker = this.displayItemTrackers.getIfPresent(location);
        if (itemTracker == null) {
            NamespacedKey vanillaLootTableKey;
            if (NMSUtil.isPaper()) {
                if (block.getState(false) instanceof Vault vault) {
                    vanillaLootTableKey = vault.getLootTable().getKey();
                } else {
                    vanillaLootTableKey = null;
                }
            } else {
                if (block.getState() instanceof Vault vault) {
                    vanillaLootTableKey = vault.getLootTable().getKey();
                } else {
                    vanillaLootTableKey = null;
                }
            }

            // Only parse the top level conditions of all VAULT loot tables and display all items when the conditions pass
            // Ignore inner loot table conditions as those are more complex to parse and are usually chance based, which we want to ignore anyway
            LootTableManager lootTableManager = this.rosePlugin.getManager(LootTableManager.class);
            LootContext context = LootContext.builder()
                    .put(LootContextParams.ORIGIN, block.getLocation())
                    .put(LootContextParams.LOOTED_BLOCK, block)
                    .put(LootContextParams.VANILLA_LOOT_TABLE_KEY, vanillaLootTableKey)
                    .build();
            List<LootTable> lootTables = lootTableManager.getLootTables(LootTableTypes.VAULT).stream()
                    .filter(lootTable -> lootTable.check(context))
                    .toList();
            boolean overwrite = lootTables.stream().anyMatch(x -> x.getOverwriteExistingValues().contains(OverwriteExisting.ITEMS));
            List<ItemStack> displayItems = lootTables.stream()
                    .flatMap(lootTable -> lootTable.getAllItems(context).stream())
                    .toList();

            itemTracker = new DisplayItemTracker(displayItems, overwrite);
            this.displayItemTrackers.put(location, itemTracker);
        }

        if (!itemTracker.passesOverwrite())
            return;

        ItemStack displayItem = itemTracker.getNextItem();
        if (displayItem != null)
            event.setDisplayItem(displayItem);
    }

    @Override
    public void enable() {
        super.enable();
        this.disabledWorlds.addAll(this.rosePlugin.getRoseConfig().get(SettingKey.DISABLED_WORLDS).stream().map(String::toLowerCase).toList());
    }

    @Override
    public void disable() {
        super.disable();
        this.displayItemTrackers.invalidateAll();
        this.disabledWorlds.clear();
    }

    private static class DisplayItemTracker {

        private final List<ItemStack> items;
        private final boolean overwrite;
        private int currentIndex;

        private DisplayItemTracker(List<ItemStack> items, boolean overwrite) {
            this.items = items;
            this.overwrite = overwrite;
            this.currentIndex = 0;
        }

        @Nullable
        public ItemStack getNextItem() {
            if (this.items.isEmpty())
                return null;

            ItemStack itemStack = this.items.get(this.currentIndex);
            this.currentIndex = (this.currentIndex + 1) % this.items.size();
            return itemStack;
        }

        public boolean passesOverwrite() {
            return this.overwrite || LootUtils.checkChance(this.getOverwriteChance());
        }

        private double getOverwriteChance() {
            if (this.items.isEmpty())
                return 0;
            return Math.min(Math.log10(this.items.size() / 3.0 + 1), 0.9); // Just approximate a chance based on the number of items
        }

    }

}
