package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.provider.NumberProvider;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;

public class ExperienceLootItem implements ExperienceGenerativeLootItem<ExperienceLootItem> {

    private final NumberProvider amount;
    private final NumberProvider equipmentBonus;

    public ExperienceLootItem(NumberProvider amount, NumberProvider equipmentBonus) {
        this.amount = amount;
        this.equipmentBonus = equipmentBonus;
    }

    @Override
    public int generate(LootContext context, List<ExperienceLootItem> others) {
        // Sum amounts
        int amount = this.amount.getInteger(context);
        amount += others.stream().mapToInt(x -> x.amount.getInteger(context)).sum();

        // Add equipment bonuses
        amount += this.sumEquipmentBonuses(context);
        amount += others.stream().mapToInt(x -> x.sumEquipmentBonuses(context)).sum();

        context.addPlaceholder("experience_amount", amount);

        return amount;
    }

    protected int sumEquipmentBonuses(LootContext context) {
        int amount = 0;
        Optional<LivingEntity> lootedEntity = context.get(LootContextParams.LOOTED_ENTITY);
        if (lootedEntity.isEmpty())
            return amount;

        EntityEquipment equipment = lootedEntity.get().getEquipment();
        if (equipment == null)
            return amount;

        long equipmentAmount = Arrays.stream(EquipmentSlot.values())
                .filter(x -> equipment.getItem(x).getType() != Material.AIR)
                .count();

        for (int i = 0; i < equipmentAmount; i++)
            amount += this.equipmentBonus.getInteger(context);

        return amount;
    }

    public static ExperienceLootItem fromSection(ConfigurationSection section) {
        NumberProvider amount = NumberProvider.fromSection(section, "amount", 0);
        NumberProvider equipmentBonus = NumberProvider.fromSection(section, "equipment-bonus", 0);
        return new ExperienceLootItem(amount, equipmentBonus);
    }

}
