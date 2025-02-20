package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.NamespacedKey;

public class VanillaLootTableCondition extends BaseLootCondition {

    private List<NamespacedKey> vanillaLootTableKeys;

    public VanillaLootTableCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        return context.get(LootContextParams.VANILLA_LOOT_TABLE_KEY)
                .filter(this.vanillaLootTableKeys::contains)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        this.vanillaLootTableKeys = new ArrayList<>();

        for (String value : values) {
            try {
                this.vanillaLootTableKeys.add(NamespacedKey.fromString(value));
            } catch (Exception ignored) { }
        }

        return !this.vanillaLootTableKeys.isEmpty();
    }

}
