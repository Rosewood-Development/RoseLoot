package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import org.bukkit.NamespacedKey;

public class VanillaLootTableCondition extends LootCondition {

    private NamespacedKey vanillaLootTableKey;

    public VanillaLootTableCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return context.getVanillaLootTableKey() != null && context.getVanillaLootTableKey().equals(this.vanillaLootTableKey);
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length != 1)
            return false;

        try {
            this.vanillaLootTableKey = NamespacedKey.minecraft(values[0]);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
