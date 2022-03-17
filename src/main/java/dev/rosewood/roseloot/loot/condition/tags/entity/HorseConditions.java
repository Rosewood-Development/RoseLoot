package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;

public class HorseConditions extends EntityConditions {

    public HorseConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("horse-armored", HorseArmoredCondition.class);
        event.registerLootCondition("horse-style", HorseStyleCondition.class);
        event.registerLootCondition("horse-color", HorseColorCondition.class);
    }

    public static class HorseArmoredCondition extends LootCondition {

        private List<HorseArmorType> armorTypes;

        public HorseArmoredCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            if (this.armorTypes.isEmpty())
                return true;

            return context.getAs(LootContextParams.LOOTED_ENTITY, Horse.class)
                    .map(Horse::getInventory)
                    .map(HorseInventory::getArmor)
                    .map(HorseArmorType::from)
                    .filter(this.armorTypes::contains)
                    .isPresent();
        }

        @Override
        public boolean parseValues(String[] values) {
            this.armorTypes = new ArrayList<>();

            for (String value : values) {
                try {
                    HorseArmorType armorType = HorseArmorType.from(value);
                    if (armorType != null)
                        this.armorTypes.add(armorType);
                } catch (Exception ignored) { }
            }

            return true;
        }

        private enum HorseArmorType {

            DIAMOND("DIAMOND_HORSE_ARMOR"),
            GOLD("GOLDEN_HORSE_ARMOR"),
            IRON("IRON_HORSE_ARMOR"),
            LEATHER("LEATHER_HORSE_ARMOR");

            private final String material;

            HorseArmorType(String material) {
                this.material = material;
            }

            public static HorseArmorType from(ItemStack item) {
                if (item == null)
                    return null;

                for (HorseArmorType value : values())
                    if (value.material.equalsIgnoreCase(item.getType().name()))
                        return value;
                return null;
            }

            public static HorseArmorType from(String name) {
                for (HorseArmorType value : values())
                    if (value.name().equalsIgnoreCase(name) || value.material.equalsIgnoreCase(name))
                        return value;
                return null;
            }

        }

    }

    public static class HorseStyleCondition extends LootCondition {

        private List<Horse.Style> types;

        public HorseStyleCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            return context.getAs(LootContextParams.LOOTED_ENTITY, Horse.class)
                    .map(Horse::getStyle)
                    .filter(this.types::contains)
                    .isPresent();
        }

        @Override
        public boolean parseValues(String[] values) {
            this.types = new ArrayList<>();

            for (String value : values) {
                try {
                    Horse.Style style = Horse.Style.valueOf(value.toUpperCase());
                    this.types.add(style);
                } catch (Exception ignored) { }
            }

            return !this.types.isEmpty();
        }

    }

    public static class HorseColorCondition extends LootCondition {

        private List<Horse.Color> colors;

        public HorseColorCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            return context.getAs(LootContextParams.LOOTED_ENTITY, Horse.class)
                    .map(Horse::getColor)
                    .filter(this.colors::contains)
                    .isPresent();
        }

        @Override
        public boolean parseValues(String[] values) {
            this.colors = new ArrayList<>();

            for (String value : values) {
                try {
                    Horse.Color style = Horse.Color.valueOf(value.toUpperCase());
                    this.colors.add(style);
                } catch (Exception ignored) { }
            }

            return !this.colors.isEmpty();
        }

    }

}
