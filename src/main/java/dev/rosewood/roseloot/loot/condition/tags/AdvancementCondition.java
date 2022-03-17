package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import org.bukkit.NamespacedKey;

public class AdvancementCondition extends LootCondition {

    private NamespacedKey advancementKey;

    public AdvancementCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return context.get(LootContextParams.ADVANCEMENT_KEY)
                .filter(this.advancementKey::equals)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length != 1)
            return false;

        try {
            this.advancementKey = NamespacedKey.fromString(values[0]);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
