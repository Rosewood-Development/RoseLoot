package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.util.NumberProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;

public class ExperienceLootItem implements LootItem<Integer> {

    private final List<NumberProvider> amounts;
    private final List<NumberProvider> equipmentBonuses;

    public ExperienceLootItem(NumberProvider amount, NumberProvider equipmentBonus) {
        this.amounts = new ArrayList<>(Collections.singletonList(amount));
        this.equipmentBonuses = new ArrayList<>(Collections.singletonList(equipmentBonus));
    }

    @Override
    public Integer create(LootContext context) {
        int amount = this.amounts.stream().mapToInt(NumberProvider::getInteger).sum();

        Optional<LivingEntity> lootedEntity = context.get(LootContextParams.LOOTED_ENTITY);
        if (lootedEntity.isPresent() && !this.equipmentBonuses.isEmpty()) {
            EntityEquipment equipment = lootedEntity.get().getEquipment();
            if (equipment != null) {
                long equipmentAmount = Arrays.stream(EquipmentSlot.values())
                        .filter(x -> equipment.getItem(x).getType() != Material.AIR)
                        .count();

                for (int i = 0; i < equipmentAmount; i++)
                    for (NumberProvider equipmentBonus : this.equipmentBonuses)
                        amount += equipmentBonus.getInteger();
            }
        }

        context.getPlaceholders().add("experience_amount", amount);

        return amount;
    }

    @Override
    public boolean combineWith(LootItem<?> lootItem) {
        if (!(lootItem instanceof ExperienceLootItem))
            return false;

        ExperienceLootItem other = (ExperienceLootItem) lootItem;
        this.amounts.addAll(other.amounts);
        this.equipmentBonuses.addAll(other.equipmentBonuses);
        return true;
    }

    public static ExperienceLootItem fromSection(ConfigurationSection section) {
        NumberProvider amount = NumberProvider.fromSection(section, "amount", 0);
        NumberProvider equipmentBonus = NumberProvider.fromSection(section, "equipment-bonus", 0);
        return new ExperienceLootItem(amount, equipmentBonus);
    }

}
