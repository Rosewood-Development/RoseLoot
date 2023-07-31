package dev.rosewood.roseloot.loot.condition;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.condition.predicate.AndLootCondition;
import dev.rosewood.roseloot.loot.condition.predicate.InvertedLootCondition;
import dev.rosewood.roseloot.loot.condition.predicate.OrLootCondition;
import dev.rosewood.roseloot.manager.LootConditionManager;
import java.util.ArrayDeque;
import java.util.Deque;

// Uses the Shunting Yard algorithm to tokenize the input string
public final class LootConditionParser {

    private static final LootConditionManager LOOT_CONDITION_MANAGER = RoseLoot.getInstance().getManager(LootConditionManager.class);
    private static final String TOKEN_REGEX = "(?<=&&)|(?=&&)|(?<=\\|\\|)|(?=\\|\\|)|(?<=!)|(?=!)|(?<=\\()|(?=\\()|(?<=\\))|(?=\\))";

    private LootConditionParser() {

    }

    /**
     * Parses a condition string into a LootCondition supporting the following operators:
     *   - AND: &&
     *   - OR: ||
     *   - NOT: !
     *   - PARENTHESIS: ()
     *
     * @param condition The condition string
     * @return The parsed LootCondition or null if the condition string is invalid
     */
    public static LootCondition parse(String condition) {
        try {
            String[] tokens = condition.split(TOKEN_REGEX);

            Deque<LootCondition> conditions = new ArrayDeque<>();
            Deque<String> operators = new ArrayDeque<>();
            for (String token : tokens) {
                switch (token) {
                    case "(" -> operators.push(token);
                    case ")" -> {
                        while (!operators.peek().equals("(")) {
                            conditions.push(getLootCondition(operators.pop(), conditions));
                        }
                        operators.pop();
                    }
                    case "&&", "||", "!" -> {
                        while (!operators.isEmpty() && hasPrecedence(token, operators.peek())) {
                            conditions.push(getLootCondition(operators.pop(), conditions));
                        }
                        operators.push(token);
                    }
                    default -> conditions.push(LOOT_CONDITION_MANAGER.parse(token.replaceAll(" ", "")));
                }
            }

            while (!operators.isEmpty())
                conditions.push(getLootCondition(operators.pop(), conditions));

            return conditions.pop();
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean hasPrecedence(String op1, String op2) {
        if (op2.equals("(") || op2.equals(")"))
            return false;
        return (!op1.equals("&&") && !op1.equals("||")) || !op2.equals("!");
    }

    private static LootCondition getLootCondition(String operator, Deque<LootCondition> conditions) {
        return switch (operator) {
            case "&&" -> new AndLootCondition(conditions.pop(), conditions.pop());
            case "||" -> new OrLootCondition(conditions.pop(), conditions.pop());
            case "!" -> new InvertedLootCondition(conditions.pop());
            default -> throw new IllegalArgumentException("Invalid operator");
        };
    }

}
