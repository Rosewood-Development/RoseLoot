package dev.rosewood.roseloot.loot;

public enum LootTableType {

    ENTITY,
    BLOCK,
    FISHING,
    CONTAINER,
    LOOT_TABLE;

    public static LootTableType fromString(String name) {
        for (LootTableType value : values())
            if (value.name().equalsIgnoreCase(name))
                return value;
        return null;
    }

}
