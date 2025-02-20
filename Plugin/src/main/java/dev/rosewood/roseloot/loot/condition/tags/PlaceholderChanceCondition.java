package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.LootUtils;

public class PlaceholderChanceCondition extends BaseLootCondition {

    private String placeholder;

    public PlaceholderChanceCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        String value = context.applyPlaceholders(this.placeholder);
        Double chance = this.parseChance(value);
        if (chance == null)
            return false;
        return LootUtils.checkChance(chance);
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length == 0)
            return false;

        this.placeholder = String.join(",", values);
        int first = this.placeholder.indexOf('%');
        int last = this.placeholder.lastIndexOf('%');
        return first != last;
    }

    private Double parseChance(String value) {
        try {
            int divisor;
            if (value.endsWith("%")) {
                value = value.substring(0, value.length() - 1);
                divisor = 100;
            } else {
                divisor = 1;
            }
            return Double.parseDouble(value) / divisor;
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
