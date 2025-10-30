package dev.rosewood.roseloot.loot;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.LootConditionParser;
import dev.rosewood.roseloot.loot.condition.predicate.AndLootCondition;
import dev.rosewood.roseloot.loot.condition.predicate.InvertedLootCondition;
import dev.rosewood.roseloot.loot.condition.predicate.OrLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.manager.LootTableManager;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LootConditionParserTest {

    private static class TestLootCondition extends BaseLootCondition {

        private List<String> arguments;

        public TestLootCondition(String tag) throws IllegalArgumentException {
            super(tag);
        }

        @Override
        public boolean check(LootContext context) {
            return !this.arguments.isEmpty();
        }

        @Override
        protected boolean parseValues(String[] values) {
            this.arguments = Arrays.asList(values);
            return true;
        }

        public List<String> getArguments() {
            return this.arguments;
        }

    }

    private static LootConditionParser PARSER;

    @BeforeAll
    public static void setup() {
        LootTableManager lootTableManager = mock(LootTableManager.class);
        when(lootTableManager.parseCondition(matches("[a-z-]+(:.+)?"))).thenAnswer(invocation -> {
            String tag = invocation.getArgument(0);
            return new TestLootCondition(tag);
        });

        PARSER = new LootConditionParser(lootTableManager);
    }

    @Test
    public void testParseNoArgs() {
        LootCondition condition = PARSER.parse("condition");
        assertNotNull(condition);
    }

    @Test
    public void testParseArgs() {
        LootCondition condition = PARSER.parse("condition:value1,value2");
        assertNotNull(condition);
    }

    @Test
    public void testInvertedConditionNoArgs() {
        LootCondition condition = PARSER.parse("!condition");
        assertNotNull(condition);
        assertInstanceOf(InvertedLootCondition.class, condition);
    }

    @Test
    public void testInvertedConditionArgs() {
        LootCondition condition = PARSER.parse("!condition:value1,value2");
        assertNotNull(condition);
        assertInstanceOf(InvertedLootCondition.class, condition);
    }

    @Test
    public void testBraces() {
        LootCondition condition = PARSER.parse("(condition)");
        assertNotNull(condition);
    }

    @Test
    public void testInvertedBraces() {
        LootCondition condition = PARSER.parse("!(condition)");
        assertInstanceOf(InvertedLootCondition.class, condition);
    }

    @Test
    public void testAnd() {
        LootCondition condition = PARSER.parse("condition && !condition:value");
        assertInstanceOf(AndLootCondition.class, condition);
        AndLootCondition andCondition = (AndLootCondition) condition;
        assertInstanceOf(TestLootCondition.class, andCondition.left());
        assertInstanceOf(InvertedLootCondition.class, andCondition.right());
    }

    @Test
    public void testOr() {
        LootCondition condition = PARSER.parse("condition || !condition:value");
        assertInstanceOf(OrLootCondition.class, condition);
        OrLootCondition orCondition = (OrLootCondition) condition;
        assertInstanceOf(TestLootCondition.class, orCondition.left());
        assertInstanceOf(InvertedLootCondition.class, orCondition.right());
    }

    @Test
    public void testPlaceholdersInConditionsRetainSpacesAndCurlyBraces() {
        String placeholder = "%placeholder_with two spaces_and_{spaced curly braces}%";
        LootCondition condition = PARSER.parse("condition:" + placeholder);
        assertInstanceOf(TestLootCondition.class, condition);
        TestLootCondition testCondition = (TestLootCondition) condition;
        assertEquals(placeholder, testCondition.getArguments().getFirst());
    }

    @Test
    public void testCurlyBracesProvideRawText() {
        String rawText = "!inner-condition:%placeholder with spaces%";
        LootCondition condition = PARSER.parse("condition:{" + rawText + "}");
        assertInstanceOf(TestLootCondition.class, condition);
        TestLootCondition testCondition = (TestLootCondition) condition;
        assertEquals(rawText, testCondition.getArguments().getFirst());
    }

    @Test
    public void testPlaceholderWithExtraPercentageSign() {
        String placeholder = "%placeholder with space {}%%";
        LootCondition condition = PARSER.parse("placeholder-chance:" + placeholder);
        assertInstanceOf(TestLootCondition.class, condition);
        TestLootCondition testCondition = (TestLootCondition) condition;
        assertEquals(placeholder, testCondition.getArguments().getFirst());
    }

}
