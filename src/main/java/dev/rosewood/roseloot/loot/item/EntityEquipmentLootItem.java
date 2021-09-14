package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.NumberProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class EntityEquipmentLootItem extends ItemLootItem {

    private final NumberProvider helmetDropChance;
    private final NumberProvider chestplateDropChance;
    private final NumberProvider leggingsDropChance;
    private final NumberProvider bootsDropChance;
    private final boolean dropInventory;

    public EntityEquipmentLootItem(NumberProvider helmetDropChance, NumberProvider chestplateDropChance, NumberProvider leggingsDropChance, NumberProvider bootsDropChance, boolean dropInventory) {
        super();
        this.helmetDropChance = helmetDropChance;
        this.chestplateDropChance = chestplateDropChance;
        this.leggingsDropChance = leggingsDropChance;
        this.bootsDropChance = bootsDropChance;
        this.dropInventory = dropInventory;
    }

    @Override
    public List<ItemStack> create(LootContext context) {
        List<ItemStack> droppedEquipment = new ArrayList<>();
        if (context.getLootedEntity() == null)
            return droppedEquipment;

        LivingEntity entity = context.getLootedEntity();

        if (this.dropInventory && entity instanceof InventoryHolder) {
            Inventory inventory = ((InventoryHolder) entity).getInventory();
            droppedEquipment.addAll(Arrays.asList(inventory.getStorageContents()));
        }

        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null)
            return droppedEquipment;

        double helmetChance = this.helmetDropChance != null ? this.helmetDropChance.getDouble() / 100 : equipment.getHelmetDropChance();
        double chestplateChance = this.chestplateDropChance != null ? this.chestplateDropChance.getDouble() / 100 : equipment.getChestplateDropChance();
        double leggingsChance = this.leggingsDropChance != null ? this.leggingsDropChance.getDouble() / 100 : equipment.getLeggingsDropChance();
        double bootsChance = this.bootsDropChance != null ? this.bootsDropChance.getDouble() / 100 : equipment.getBootsDropChance();

        if (equipment.getBoots() != null && LootUtils.checkChance(helmetChance))
            droppedEquipment.add(equipment.getBoots());

        if (equipment.getChestplate() != null && LootUtils.checkChance(chestplateChance))
            droppedEquipment.add(equipment.getChestplate());

        if (equipment.getLeggings() != null && LootUtils.checkChance(leggingsChance))
            droppedEquipment.add(equipment.getLeggings());

        if (equipment.getBoots() != null && LootUtils.checkChance(bootsChance))
            droppedEquipment.add(equipment.getBoots());

        return droppedEquipment;
    }

    public static EntityEquipmentLootItem fromSection(ConfigurationSection section) {
        NumberProvider helmetDropChance = NumberProvider.fromSection(section, "helmet-drop-chance", null);
        NumberProvider chestplateDropChance = NumberProvider.fromSection(section, "chestplate-drop-chance", null);
        NumberProvider leggingsDropChance = NumberProvider.fromSection(section, "leggings-drop-chance", null);
        NumberProvider bootsDropChance = NumberProvider.fromSection(section, "boots-drop-chance", null);
        boolean dropInventory = section.getBoolean("drop-inventory", false);
        return new EntityEquipmentLootItem(helmetDropChance, chestplateDropChance, leggingsDropChance, bootsDropChance, dropInventory);
    }

}
