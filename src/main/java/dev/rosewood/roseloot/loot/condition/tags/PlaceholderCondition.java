package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.rosegarden.hook.PlaceholderAPIHook;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.function.BiFunction;
import org.bukkit.entity.Player;

public class PlaceholderCondition extends LootCondition {

    private String left, right;
    private Operator operator;

    public PlaceholderCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        if (!PlaceholderAPIHook.enabled())
            return false;

        Player player = null;
        if (context.getLooter() instanceof Player)
            player = (Player) context.getLooter();

        return this.operator.evaluate(PlaceholderAPIHook.applyPlaceholders(player, this.left), PlaceholderAPIHook.applyPlaceholders(player, this.right));
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

    private enum Operator {
        NOT_EQUALS("!=", (left, right) -> !left.equalsIgnoreCase(right)),
        LESS_THAN_OR_EQUALS("<=", (left, right) -> Double.parseDouble(left) <= Double.parseDouble(right)),
        GREATER_THAN_OR_EQUALS(">=", (left, right) -> Double.parseDouble(left) >= Double.parseDouble(right)),

        EQUALS("=", String::equalsIgnoreCase),
        LESS_THAN("<", (left, right) -> Double.parseDouble(left) < Double.parseDouble(right)),
        GREATER_THAN(">", (left, right) -> Double.parseDouble(left) > Double.parseDouble(right)),
        CONTAINS("^", (left, right) -> left.toLowerCase().contains(right.toLowerCase()));

        private final String symbol;
        private final BiFunction<String, String, Boolean> operation;

        Operator(String symbol, BiFunction<String, String, Boolean> operation) {
            this.symbol = symbol;
            this.operation = operation;
        }

        public String getSymbol() {
            return this.symbol;
        }

        public boolean evaluate(String left, String right) {
            try {
                return this.operation.apply(left, right);
            } catch (Exception e) {
                return false;
            }
        }
    }

}
