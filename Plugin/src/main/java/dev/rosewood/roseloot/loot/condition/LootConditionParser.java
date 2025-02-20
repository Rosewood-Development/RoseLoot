package dev.rosewood.roseloot.loot.condition;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.condition.predicate.AndLootCondition;
import dev.rosewood.roseloot.loot.condition.predicate.InvertedLootCondition;
import dev.rosewood.roseloot.loot.condition.predicate.OrLootCondition;
import dev.rosewood.roseloot.manager.LootTableManager;
import java.util.ArrayDeque;
import java.util.Deque;

// Uses the Shunting Yard algorithm to tokenize the input string
public final class LootConditionParser {

    private static final String TOKEN_REGEX = "(?<=&&)|(?=&&)|(?<=\\|\\|)|(?=\\|\\|)|(?<=(?<=^|\\s|\\()!)|(?=(?<=^|\\s|\\()!)|(?<=\\()|(?=\\()|(?<=\\))|(?=\\))";

    private LootConditionParser() {

    }

    /**
     * Parses a condition String into a {@link LootCondition} supporting the following operators:
     * <ul>
     *     <li><code>AND: &&</code></li>
     *     <li><code>OR: ||</code></li>
     *     <li><code>NOT: !</code></li>
     *     <li><code>PARENTHESIS: ()</code></li>
     * </ul>
     *
     * Any spaces are purely aesthetic and will be removed.
     *
     * @param condition The condition String to parse
     * @return The parsed {@link LootCondition} or <code>null</code> if the condition String is invalid
     */
    public static LootCondition parse(String condition) {
        condition = condition.replaceAll("\\s+", "");

        LootTableManager lootTableManager = RoseLoot.getInstance().getManager(LootTableManager.class);
        try {
            String[] tokens = condition.split(TOKEN_REGEX);

            Deque<LootCondition> conditions = new ArrayDeque<>();
            Deque<String> operators = new ArrayDeque<>();
            for (String token : tokens) {
                switch (token) {
                    case "(" -> operators.push(token);
                    case ")" -> {
                        while (!operators.peek().equals("("))
                            conditions.push(getLootCondition(operators.pop(), conditions));
                        operators.pop();
                    }
                    case "&&", "||", "!" -> {
                        while (!operators.isEmpty() && hasPrecedence(token, operators.peek()))
                            conditions.push(getLootCondition(operators.pop(), conditions));
                        operators.push(token);
                    }
                    default -> conditions.push(lootTableManager.parseCondition(token));
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
