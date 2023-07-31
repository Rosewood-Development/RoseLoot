package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.NamespacedKey;

public class AdvancementCondition extends BaseLootCondition {

    private List<NamespacedKey> advancementKeys;

    public AdvancementCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        return context.get(LootContextParams.ADVANCEMENT_KEY)
                .filter(this.advancementKeys::contains)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        this.advancementKeys = new ArrayList<>();

        for (String value : values) {
            try {
                this.advancementKeys.add(NamespacedKey.fromString(value));
            } catch (Exception ignored) { }
        }

        return !this.advancementKeys.isEmpty();
    }

}
