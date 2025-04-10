package dev.rosewood.roseloot.loot.item;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.VersionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LootTableLootItem implements RecursiveLootItem {

    private final String lootTableName;
    private boolean invalid;

    private LootTable lootTable;
    private org.bukkit.loot.LootTable vanillaLootTable;

    protected LootTableLootItem(LootTable lootTable) {
        this.lootTableName = lootTable.getName();
        this.lootTable = lootTable;
    }

    protected LootTableLootItem(String lootTableName) {
        this.lootTableName = lootTableName;
    }

    @Override
    public List<LootItem> generate(LootContext context) {
        if (this.invalid)
            return List.of();

        this.resolveLootTable();
        if (this.invalid)
            return List.of();

        Optional<LootTable> currentLootTableOptional = context.getCurrentLootTable();
        if (this.lootTable != null && currentLootTableOptional.isPresent()) {
            LootTable currentLootTable = currentLootTableOptional.get();
            if (currentLootTable.equals(this.lootTable) && !this.lootTable.allowsRecursion()) {
                RoseLoot.getInstance().getLogger().severe("Detected and blocked potential infinite recursion for loot table: " + this.lootTableName + ". " +
                        "This loot table will be empty unless the recursion issue is fixed. If recursion was intentional, you can set `allow-recursion: true` " +
                        "in the loot table file to allow it. Please note this can create the potential to crash your server if you create an infinite loop.");
                return List.of();
            }
        }

        List<LootItem> lootItems;
        if (this.lootTable != null) {
            if (this.lootTable.check(context)) {
                LootTable currentLootTable = context.getCurrentLootTable().orElse(null);
                LootContents lootContents = new LootContents(context);
                this.lootTable.populate(context, lootContents);
                lootItems = lootContents.getContents();
                context.setCurrentLootTable(currentLootTable);
            } else {
                lootItems = List.of();
            }
        } else {
            int lootingModifier = 0;
            Optional<ItemStack> itemUsed = context.getItemUsed();
            if (itemUsed.isPresent()) {
                ItemStack item = itemUsed.get();
                if (item.containsEnchantment(VersionUtils.LOOTING)) {
                    lootingModifier = item.getEnchantmentLevel(VersionUtils.LOOTING);
                } else if (item.containsEnchantment(VersionUtils.FORTUNE)) {
                    lootingModifier = item.getEnchantmentLevel(VersionUtils.FORTUNE);
                }
            }

            try {
                Optional<Location> origin = context.get(LootContextParams.ORIGIN);
                if (origin.isEmpty()) {
                    return List.of();
                }

                Player lootingPlayer = context.getLootingPlayer().orElse(null);
                org.bukkit.loot.LootContext vanillaContext = new org.bukkit.loot.LootContext.Builder(origin.get())
                        .lootedEntity(context.get(LootContextParams.LOOTED_ENTITY).orElse(lootingPlayer))
                        .killer(lootingPlayer)
                        .lootingModifier(lootingModifier)
                        .luck((float) context.getLuckLevel())
                        .build();

                lootItems = List.of(new VanillaItemLootItem(this.vanillaLootTable.populateLoot(LootUtils.RANDOM, vanillaContext)));
            } catch (Exception e) {
                RoseLoot.getInstance().getLogger().warning("Failed to generate loot from vanilla loot table: [" + this.vanillaLootTable.getKey() + "]. Reason: " + e.getMessage());
                if (e.getMessage().contains("<parameter minecraft:tool>"))
                    RoseLoot.getInstance().getLogger().warning("Vanilla fishing loot tables cannot currently run properly. Please provide RoseLoot versions of the vanilla fishing loot tables instead if you wish to use them.");
                lootItems = List.of();
            }
        }

        return lootItems;
    }

    private void resolveLootTable() {
        if (this.lootTable == null && this.vanillaLootTable == null) {
            RosePlugin rosePlugin = RoseLoot.getInstance();
            this.lootTable = rosePlugin.getManager(LootTableManager.class).getLootTable(LootTableTypes.LOOT_TABLE, this.lootTableName);

            if (this.lootTable == null) {
                NamespacedKey key = NamespacedKey.fromString(this.lootTableName);
                if (key != null)
                    this.vanillaLootTable = Bukkit.getLootTable(key);

                if (this.vanillaLootTable == null) {
                    this.invalid = true;
                    rosePlugin.getLogger().warning("Could not find loot table specified: " + this.lootTableName);
                }
            }
        }
    }

    @Override
    public boolean check(LootContext context) {
        this.resolveLootTable();
        if (this.invalid || this.lootTable == null)
            return true;
        return this.lootTable.check(context);
    }

    public static LootTableLootItem fromSection(ConfigurationSection section) {
        if (section.isString("value"))
            return new LootTableLootItem(section.getString("value"));

        try {
            LootTable loadedLootTable = RoseLoot.getInstance().getManager(LootTableManager.class).loadConfiguration("$internal", "$internal", section);
            if (loadedLootTable != null)
                return new LootTableLootItem(loadedLootTable);
        } catch (Exception e) {
            RoseLoot.getInstance().getLogger().warning("Failed to load internal loot table: " + e.getMessage());
        }
        return null;
    }

    private static class VanillaItemLootItem implements ItemGenerativeLootItem {

        private final Collection<ItemStack> items;

        public VanillaItemLootItem(Collection<ItemStack> items) {
            this.items = items;
        }

        @Override
        public List<ItemStack> generate(LootContext context) {
            return this.getAllItems(context);
        }

        @Override
        public List<ItemStack> getAllItems(LootContext context) {
            return new ArrayList<>(this.items);
        }

    }

}
