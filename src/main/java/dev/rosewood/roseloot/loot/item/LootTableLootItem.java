package dev.rosewood.roseloot.loot.item;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.NumberProvider;
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

public class LootTableLootItem implements LootItem<List<LootItem<?>>> {

    private final String lootTableName;
    private boolean invalid;
    private LootTable lootTable;
    private org.bukkit.loot.LootTable vanillaLootTable;
    private boolean running;

    public LootTableLootItem(String lootTableName) {
        this.lootTableName = lootTableName;
    }

    @Override
    public List<LootItem<?>> create(LootContext context) {
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

        if (this.running) {
            RoseLoot.getInstance().getLogger().severe("Detected and blocked potential infinite recursion for loot table: " + this.lootTableName + ". " +
                    "This loot table will be empty and log this error message until fixed.");
            this.running = false;
            return List.of();
        }

        this.running = true;
        List<LootItem<?>> lootItems;
        if (this.lootTable != null) {
            lootItems = this.lootTable.generate(context);
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

    private static class VanillaItemLootItem extends ItemLootItem {

        private final Collection<ItemStack> items;

        public VanillaItemLootItem(Collection<ItemStack> items) {
            super(null, NumberProvider.constant(-1), NumberProvider.constant(-1), List.of(), null, null, false, null);
            this.items = items;
        }

        @Override
        public List<ItemStack> create(LootContext context) {
            return new ArrayList<>(this.items);
        }

    }

}
