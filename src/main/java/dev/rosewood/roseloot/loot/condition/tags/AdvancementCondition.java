package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import org.bukkit.NamespacedKey;

public class AdvancementCondition extends LootCondition {

    private NamespacedKey advancementKey;

    public AdvancementCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return context.getAdvancementKey() != null && context.getAdvancementKey().equals(this.advancementKey);
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length != 1)
            return false;

        try {
            this.advancementKey = NamespacedKey.minecraft(values[0]);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
