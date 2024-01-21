package dev.rosewood.roseloot.hook.conditions;

import dev.rosewood.roseloot.listener.hook.ItemsAdderBlockBreakListener;
import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.Arrays;
import java.util.List;

public class ItemsAdderBlockCondition extends BaseLootCondition {

    private List<String> blockTypes;

    public ItemsAdderBlockCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        return context.get(ItemsAdderBlockBreakListener.ITEMSADDER_BLOCK)
                .filter(this.blockTypes::contains)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        this.blockTypes = Arrays.asList(values);
        return !this.blockTypes.isEmpty();
    }

}
