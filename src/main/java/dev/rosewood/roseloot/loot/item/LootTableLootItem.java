package dev.rosewood.roseloot.loot.item;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.loot.LootTableType;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.NumberProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
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
            return Collections.emptyList();

        if (this.lootTable == null && this.vanillaLootTable == null) {
            RosePlugin rosePlugin = RoseLoot.getInstance();
            this.lootTable = rosePlugin.getManager(LootTableManager.class).getLootTable(LootTableType.LOOT_TABLE, this.lootTableName);
            if (this.lootTable == null) {
                NamespacedKey key = NamespacedKey.fromString(this.lootTableName);
                if (key != null)
                    this.vanillaLootTable = Bukkit.getLootTable(key);

                if (this.vanillaLootTable == null) {
                    this.invalid = true;
                    rosePlugin.getLogger().warning("Could not find loot table specified: " + this.lootTableName);
                    return Collections.emptyList();
                }
            }
        }

        if (this.running) {
            RoseLoot.getInstance().getLogger().severe("Detected and blocked potential infinite recursion for loot table: " + this.lootTableName + ". " +
                    "This loot table will be empty and log this error message until fixed.");
            this.running = false;
            return Collections.emptyList();
        }

        this.running = true;
        List<LootItem<?>> lootItems;
        if (this.lootTable != null) {
            lootItems = this.lootTable.generate(context);
        } else {
            Location location = context.getLocation();
            World world = location.getWorld();
            if (world == null)
                return Collections.emptyList();

            int lootingModifier = 0;
            ItemStack itemUsed = context.getItemUsed();
            if (itemUsed != null) {
                if (itemUsed.containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) {
                    lootingModifier = itemUsed.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
                } else if (itemUsed.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                    lootingModifier = itemUsed.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                }
            }

            org.bukkit.loot.LootContext vanillaContext = new org.bukkit.loot.LootContext.Builder(location)
                    .lootedEntity(context.getLootedEntity())
                    .killer(context.getLootingPlayer())
                    .lootingModifier(lootingModifier)
                    .luck((float) context.getLuckLevel())
                    .build();

            lootItems = Collections.singletonList(new VanillaItemLootItem(this.vanillaLootTable.populateLoot(LootUtils.RANDOM, vanillaContext)));
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
            super(null, NumberProvider.fromSection(null, null, -1), null, null);
            this.items = items;
        }

        @Override
        public List<ItemStack> create(LootContext context) {
            return new ArrayList<>(this.items);
        }

    }

}
