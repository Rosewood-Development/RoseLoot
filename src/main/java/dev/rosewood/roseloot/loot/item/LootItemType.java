package dev.rosewood.roseloot.loot.item;

public enum LootItemType {

    ITEM,
    EXPERIENCE,
    COMMAND,
    LOOT_TABLE,
    EXPLOSION;

    public static LootItemType fromString(String name) {
        for (LootItemType value : values())
            if (value.name().equalsIgnoreCase(name))
                return value;
        return null;
    }

}
