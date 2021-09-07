package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;

public class ExperienceLootItem implements LootItem<Integer> {

    private int min;
    private int max;
    private final List<EquipmentBonus> equipmentBonuses;

    public ExperienceLootItem(int min, int max, EquipmentBonus equipmentBonus) {
        this.min = min;
        this.max = max;
        this.equipmentBonuses = new ArrayList<>(equipmentBonus != null ? Collections.singletonList(equipmentBonus) : Collections.emptyList());
    }

    @Override
    public Integer create(LootContext context) {
        int min = this.min;
        int max = this.max;
        LivingEntity entity = context.getLootedEntity();
        if (entity != null && !this.equipmentBonuses.isEmpty()) {
            EntityEquipment equipment = entity.getEquipment();
            if (equipment != null) {
                int equipmentAmount = Arrays.stream(EquipmentSlot.values())
                        .filter(x -> equipment.getItem(x).getType() != Material.AIR)
                        .mapToInt(x -> 1)
                        .sum();

                for (EquipmentBonus equipmentBonus : this.equipmentBonuses) {
                    min += equipmentAmount * equipmentBonus.getMinBonus();
                    max += equipmentAmount * equipmentBonus.getMaxBonus();
                }
            }
        }

        return LootUtils.randomInRange(min, max);
    }

    @Override
    public boolean combineWith(LootItem<?> lootItem) {
        if (!(lootItem instanceof ExperienceLootItem))
            return false;

        ExperienceLootItem other = (ExperienceLootItem) lootItem;
        this.min += other.min;
        this.max += other.max;
        this.equipmentBonuses.addAll(other.equipmentBonuses);
        return true;
    }

    public static ExperienceLootItem fromSection(ConfigurationSection section) {
        int minExp, maxExp;
        if (section.contains("amount")) {
            minExp = maxExp = section.getInt("amount");
        } else {
            minExp = section.getInt("min", 1);
            maxExp = section.getInt("max", 1);
        }

        ConfigurationSection equipmentBonusSection = section.getConfigurationSection("equipment-bonus");
        EquipmentBonus equipmentBonus = null;
        if (equipmentBonusSection != null) {
            int min, max;
            if (equipmentBonusSection.contains("amount")) {
                min = max = equipmentBonusSection.getInt("amount");
            } else {
                min = equipmentBonusSection.getInt("min", 0);
                max = equipmentBonusSection.getInt("max", 0);
            }

            equipmentBonus = new EquipmentBonus(min, max);
        }

        return new ExperienceLootItem(minExp, maxExp, equipmentBonus);
    }

    public static class EquipmentBonus {

        private final int min, max;

        public EquipmentBonus(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int getMinBonus() {
            return this.min;
        }

        public int getMaxBonus() {
            return this.max;
        }

    }

}
