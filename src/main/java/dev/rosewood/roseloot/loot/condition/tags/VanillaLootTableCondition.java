package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import org.bukkit.NamespacedKey;

public class VanillaLootTableCondition extends LootCondition {

    private NamespacedKey vanillaLootTableKey;

    public VanillaLootTableCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return context.get(LootContextParams.VANILLA_LOOT_TABLE_KEY)
                .filter(this.vanillaLootTableKey::equals)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length != 1)
            return false;

        try {
            this.vanillaLootTableKey = NamespacedKey.fromString(values[0]);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
