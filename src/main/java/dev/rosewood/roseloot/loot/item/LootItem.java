package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootGenerator;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public abstract class LootItem implements LootGenerator {

    public static LootItem fromSection(ConfigurationSection section) {
        LootItemType type = LootItemType.fromString(section.getString("type"));
        if (type == null)
            return null;

        switch (type) {
            case ITEM:
                int min, max;
                if (section.contains("amount")) {
                    min = max = section.getInt("amount");
                } else {
                    min = section.getInt("min", 1);
                    max = section.getInt("max", 1);
                }

                String itemString = section.getString("item");
                if (itemString == null)
                    return null;

                Material item = Material.matchMaterial(itemString);
                if (item == null)
                    return null;

                return new ItemLootItem(item, min, max);

            case EXPERIENCE:
                if (!section.contains("value"))
                    return null;
                return new ExperienceLootItem(section.getInt("value", 0));

            case COMMAND:
                if (!section.contains("value"))
                    return null;
                return new CommandLootItem(section.getString("value"));

            default:
                throw new IllegalStateException("Invalid LootItemType specified!");
        }
    }

}
