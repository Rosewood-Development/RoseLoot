package dev.rosewood.roseloot.loot;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public enum OverwriteExisting {

    ITEMS,
    EXPERIENCE;

    public static Set<OverwriteExisting> fromStrings(List<String> values) {
        Set<OverwriteExisting> overwriteExisting = EnumSet.noneOf(OverwriteExisting.class);
        for (String value : values) {
            try {
                overwriteExisting.add(OverwriteExisting.valueOf(value.toUpperCase()));
            } catch (IllegalArgumentException ignored) { }
        }

        return overwriteExisting;
    }

    public static Set<OverwriteExisting> all() {
        return EnumSet.allOf(OverwriteExisting.class);
    }

    public static Set<OverwriteExisting> none() {
        return EnumSet.noneOf(OverwriteExisting.class);
    }

}
