package dev.rosewood.roseloot.loot.condition;

import dev.rosewood.roseloot.loot.condition.tags.BlockTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.ChanceCondition;
import dev.rosewood.roseloot.loot.condition.tags.EntityTypeCondition;
import dev.rosewood.roseloot.loot.condition.tags.SpawnReasonCondition;
import dev.rosewood.roseloot.loot.condition.tags.WorldCondition;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LootConditions {

    private static final Map<String, Constructor<? extends LootCondition>> tagPrefixMap = new HashMap<>();
    private static final Map<String, String> tagDescriptionMap = new LinkedHashMap<>();

    static {
        registerTag("block-type", BlockTypeCondition.class, "Type the looted block must be");
        registerTag("chance", ChanceCondition.class, "A random chance");
        registerTag("entity-type", EntityTypeCondition.class, "Type the looted entity must be");
        registerTag("spawn-reason", SpawnReasonCondition.class, "Spawn reason the entity must have");
        registerTag("world", WorldCondition.class, "Loot must be generated in a specific world");
    }

    public static Map<String, String> getTagDescriptionMap() {
        return Collections.unmodifiableMap(tagDescriptionMap);
    }

    private static <T extends LootCondition> void registerTag(String prefix, Class<T> tagClass, String description) {
        try {
            tagPrefixMap.put(prefix, tagClass.getConstructor(String.class));
            if (description != null)
                tagDescriptionMap.put(prefix, description);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    public static LootCondition parse(String tag) {
        try {
            int index = tag.indexOf(":");
            if (index == -1) {
                return tagPrefixMap.get(tag).newInstance(tag);
            } else {
                return tagPrefixMap.get(tag.substring(0, index)).newInstance(tag);
            }
        } catch (Exception e) {
            return null;
        }
    }

}
