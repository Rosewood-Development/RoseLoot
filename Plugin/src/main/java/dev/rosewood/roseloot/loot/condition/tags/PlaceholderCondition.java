package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.condition.Operator;
import dev.rosewood.roseloot.loot.context.LootContext;

public class PlaceholderCondition extends BaseLootCondition {

    private String left, right;
    private Operator operator;

    public PlaceholderCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        return this.operator.evaluate(context.applyPlaceholders(this.left), context.applyPlaceholders(this.right));
    }

    @Override
    public boolean parseValues(String[] values) {
        // Piece the expression back together in case it got split up
        String expression = String.join(",", values);
        if (expression.trim().isEmpty())
            return false;

        char placeholderSymbol = '%';
        outer:
        for (Operator operator : Operator.values()) {
            String symbol = operator.getSymbol();
            boolean inPlaceholder = false;
            StringBuilder buffer = new StringBuilder();
            for (char c : expression.toCharArray()) {
                if (c == placeholderSymbol)
                    inPlaceholder = !inPlaceholder;

                buffer.append(c);
                if (!inPlaceholder && buffer.toString().endsWith(symbol)) {
                    this.left = buffer.substring(0, buffer.length() - symbol.length()).trim();
                    this.operator = operator;
                    this.right = expression.substring(this.left.length() + symbol.length()).trim();
                    break outer;
                }
            }
        }

        return this.left != null && this.right != null && this.operator != null;
    }

}
