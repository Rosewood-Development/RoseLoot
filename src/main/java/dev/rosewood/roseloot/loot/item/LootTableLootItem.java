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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class LootTableLootItem implements RecursiveLootItem {

    private final String lootTableName;
    private boolean invalid;
    private LootTable lootTable;
    private org.bukkit.loot.LootTable vanillaLootTable;
    private boolean running;

    public LootTableLootItem(String lootTableName) {
        this.lootTableName = lootTableName;
    }

    @Override
    public List<LootItem> generate(LootContext context) {
        if (this.invalid)
            return List.of();

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
                    return List.of();
                }
            }
        }

        if (this.running && !context.getCurrentLootTable().map(LootTable::allowsRecursion).orElse(false)) {
            RoseLoot.getInstance().getLogger().severe("Detected and blocked potential infinite recursion for loot table: " + this.lootTableName + ". " +
                    "This loot table will be empty unless the recursion issue is fixed. If recursion was intentional, you can set `allow-recursion: true` " +
                    "in the loot table file to allow it. Please note this can create the potential to crash your server if you create an infinite loop.");
            this.running = false;
            return List.of();
        }

        this.running = true;
        List<LootItem> lootItems;
        if (this.lootTable != null) {
            LootTable currentLootTable = context.getCurrentLootTable().orElse(null);
            LootContents lootContents = new LootContents(context);
            this.lootTable.populate(context, lootContents);
            lootItems = lootContents.getContents();
            context.setCurrentLootTable(currentLootTable);
        } else {
            int lootingModifier = 0;
            Optional<ItemStack> itemUsed = context.getItemUsed();
            if (itemUsed.isPresent()) {
                ItemStack item = itemUsed.get();
                if (item.containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) {
                    lootingModifier = item.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
                } else if (item.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                    lootingModifier = item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                }
            }

            try {
                Optional<Location> origin = context.get(LootContextParams.ORIGIN);
                if (origin.isEmpty())
                    return List.of();

                org.bukkit.loot.LootContext vanillaContext = new org.bukkit.loot.LootContext.Builder(origin.get())
                        .lootedEntity(context.get(LootContextParams.LOOTED_ENTITY).orElse(null))
                        .killer(context.getLootingPlayer().orElse(null))
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
        this.running = false;

        return lootItems;
    }

    public static LootTableLootItem fromSection(ConfigurationSection section) {
        if (!section.contains("value"))
            return null;
        return new LootTableLootItem(section.getString("value"));
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
