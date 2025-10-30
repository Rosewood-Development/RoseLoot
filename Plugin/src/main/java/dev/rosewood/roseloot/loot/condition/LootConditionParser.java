package dev.rosewood.roseloot.loot.condition;

import dev.rosewood.roseloot.loot.condition.predicate.AndLootCondition;
import dev.rosewood.roseloot.loot.condition.predicate.InvertedLootCondition;
import dev.rosewood.roseloot.loot.condition.predicate.OrLootCondition;
import dev.rosewood.roseloot.manager.LootTableManager;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;

/**
 * Uses the Shunting Yard algorithm to tokenize the input string
 */
public final class LootConditionParser {

    private final LootTableManager lootTableManager;

    @ApiStatus.Internal
    public LootConditionParser(LootTableManager lootTableManager) {
        this.lootTableManager = lootTableManager;
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
    public LootCondition parse(String condition) {
        try {
            List<String> tokens = this.tokenize(condition);

            Deque<LootCondition> conditions = new ArrayDeque<>();
            Deque<String> operators = new ArrayDeque<>();
            for (String token : tokens) {
                switch (token) {
                    case "(", "!" -> operators.push(token);
                    case ")" -> {
                        while (!operators.peek().equals("("))
                            conditions.push(this.getLootCondition(operators.pop(), conditions));
                        operators.pop();
                    }
                    case "&&", "||" -> {
                        while (!operators.isEmpty() && this.hasPrecedence(token, operators.peek()))
                            conditions.push(this.getLootCondition(operators.pop(), conditions));
                        operators.push(token);
                    }
                    default -> conditions.push(this.lootTableManager.parseCondition(token));
                }
            }

            while (!operators.isEmpty())
                conditions.push(this.getLootCondition(operators.pop(), conditions));

            return conditions.pop();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean hasPrecedence(String incomingOp, String stackOp) {
        if (stackOp.equals("(") || stackOp.equals(")"))
            return false;

        int precIncoming = this.getPrecedence(incomingOp);
        int precStack = this.getPrecedence(stackOp);

        if (incomingOp.equals("!"))
            return precIncoming < precStack;

        return precIncoming <= precStack;
    }

    private int getPrecedence(String op) {
        return switch (op) {
            case "!" -> 3;
            case "&&" -> 2;
            case "||" -> 1;
            default -> 0;
        };
    }

    private LootCondition getLootCondition(String operator, Deque<LootCondition> conditions) {
        return switch (operator) {
            case "&&" -> new AndLootCondition(conditions.pop(), conditions.pop());
            case "||" -> new OrLootCondition(conditions.pop(), conditions.pop());
            case "!" -> new InvertedLootCondition(conditions.pop());
            default -> throw new IllegalArgumentException("Invalid operator");
        };
    }

    private List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        int nextPlaceholder;
        boolean insidePlaceholder = false;
        int braceDepth = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (!insidePlaceholder) {
                if (c == '%') {
                    nextPlaceholder = input.indexOf('%', i + 1);
                    if (nextPlaceholder > i) {
                        insidePlaceholder = true;
                        currentToken.append(c);
                        continue;
                    }
                }

                if (Character.isWhitespace(c))
                    continue;

                if (c == '{') {
                    braceDepth++;
                    if (braceDepth == 1)
                        continue;
                } else if (c == '}') {
                    braceDepth--;
                    if (braceDepth == 0)
                        continue;
                }
            } else if (c == '%') {
                insidePlaceholder = false;
                currentToken.append(c);
                continue;
            }

            if (braceDepth > 0) {
                currentToken.append(c);
                continue;
            }

            if (i + 1 < input.length()) {
                String twoCharOp = input.substring(i, i + 2);
                if (twoCharOp.equals("&&") || twoCharOp.equals("||")) {
                    if (!currentToken.isEmpty()) {
                        tokens.add(currentToken.toString());
                        currentToken.setLength(0);
                    }
                    tokens.add(twoCharOp);
                    i++;
                    continue;
                }
            }

            if (c == '(' || c == ')' || c == '!') {
                if (c == '!' && i + 1 < input.length() && input.charAt(i + 1) == '=') {
                    currentToken.append("!=");
                    i++;
                    continue;
                } else {
                    if (!currentToken.isEmpty()) {
                        tokens.add(currentToken.toString());
                        currentToken.setLength(0);
                    }
                    tokens.add(String.valueOf(c));
                }
                continue;
            }

            currentToken.append(c);
        }

        if (!currentToken.isEmpty())
            tokens.add(currentToken.toString());

        return tokens;
    }

}
