package dev.rosewood.roseloot.loot.condition;

import java.util.function.BiFunction;

public enum Operator {

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
