package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.item.meta.ItemLootMeta;
import dev.rosewood.roseloot.manager.LootConditionManager;
import dev.rosewood.roseloot.util.EnchantingUtils;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.NumberProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class ItemLootItem implements LootItem<List<ItemStack>> {

    protected Material item;
    private final NumberProvider amount;
    private final NumberProvider maxAmount;
    private final ItemLootMeta itemLootMeta;
    private final ConditionalBonus conditionalBonus;
    private final EnchantmentBonus enchantmentBonus;
    private final boolean smeltIfBurning;

    public ItemLootItem(Material item, NumberProvider amount, NumberProvider maxAmount, ItemLootMeta itemLootMeta, ConditionalBonus conditionalBonus, EnchantmentBonus enchantmentBonus, boolean smeltIfBurning) {
        this.item = item;
        this.amount = amount;
        this.maxAmount = maxAmount;
        this.itemLootMeta = itemLootMeta;
        this.conditionalBonus = conditionalBonus;
        this.enchantmentBonus = enchantmentBonus;
        this.smeltIfBurning = smeltIfBurning;
    }

    protected ItemLootItem() {
        this(null, null, null, null, null, null, false);
    }

    @Override
    public List<ItemStack> create(LootContext context) {
        List<ItemStack> generatedItems = new ArrayList<>();

        int amount = this.amount.getInteger();

        if (this.conditionalBonus != null)
            amount += this.conditionalBonus.getBonusAmount(context);

        if (this.enchantmentBonus != null)
            amount += this.enchantmentBonus.getBonusAmount(context, amount);

        Material item = this.item;
        if (this.smeltIfBurning && context.getLootedEntity() != null && context.getLootedEntity().getFireTicks() > 0) {
            Iterator<Recipe> recipesIterator = Bukkit.recipeIterator();
            while (recipesIterator.hasNext()) {
                Recipe recipe = recipesIterator.next();
                if (recipe instanceof FurnaceRecipe) {
                    FurnaceRecipe furnaceRecipe = (FurnaceRecipe) recipe;
                    if (furnaceRecipe.getInput().getType() == item) {
                        item = furnaceRecipe.getResult().getType();
                        break;
                    }
                }
            }
        }

        amount = Math.min(amount, this.maxAmount.getInteger());

        if (amount > 0) {
            int maxStackSize = item.getMaxStackSize();
            ItemStack itemStack = this.itemLootMeta.apply(new ItemStack(item), context);
            while (amount > maxStackSize) {
                amount -= maxStackSize;
                ItemStack clone = itemStack.clone();
                clone.setAmount(maxStackSize);
                generatedItems.add(clone);
            }

            if (amount > 0) {
                ItemStack clone = itemStack.clone();
                clone.setAmount(amount);
                generatedItems.add(clone);
            }
        }

        return generatedItems;
    }

    public static ItemLootItem fromSection(ConfigurationSection section) {
        String itemString = section.getString("item");
        if (itemString == null)
            return null;

        Material item = Material.matchMaterial(itemString);
        if (item == null)
            return null;

        NumberProvider amount = NumberProvider.fromSection(section, "amount", 1);
        NumberProvider maxAmount = NumberProvider.fromSection(section, "max-amount", Integer.MAX_VALUE);

        ConfigurationSection conditionBonusSection = section.getConfigurationSection("conditional-bonus");
        ItemLootItem.ConditionalBonus conditionalBonus = null;
        if (conditionBonusSection != null) {
            LootConditionManager lootConditionManager = RoseLoot.getInstance().getManager(LootConditionManager.class);
            List<LootCondition> conditions = new ArrayList<>();
            for (String conditionString : conditionBonusSection.getStringList("conditions")) {
                LootCondition condition = lootConditionManager.parse(conditionString);
                if (condition != null)
                    conditions.add(condition);
            }
            NumberProvider bonusPerCondition = NumberProvider.fromSection(conditionBonusSection, "bonus-per-condition", 1);
            conditionalBonus = new ConditionalBonus(conditions, bonusPerCondition);
        }

        ConfigurationSection enchantmentBonusSection = section.getConfigurationSection("enchantment-bonus");
        ItemLootItem.EnchantmentBonus enchantmentBonus = null;
        if (enchantmentBonusSection != null) {
            BonusFormula formula = BonusFormula.fromString(enchantmentBonusSection.getString("formula", BonusFormula.UNIFORM.name()));
            String enchantmentString = enchantmentBonusSection.getString("enchantment");
            if (enchantmentString != null) {
                Enchantment enchantment = EnchantingUtils.getEnchantmentByName(enchantmentString);
                NumberProvider bonusPerLevel = NumberProvider.fromSection(enchantmentBonusSection, "bonus-per-level", 0);
                NumberProvider probability = NumberProvider.fromSection(enchantmentBonusSection, "probability", 0);
                if (enchantment != null)
                    enchantmentBonus = new ItemLootItem.EnchantmentBonus(formula, enchantment, bonusPerLevel, probability);
            }
        }

        boolean smeltIfBurning = section.getBoolean("smelt-if-burning", false);
        ItemLootMeta itemLootMeta = ItemLootMeta.fromSection(item, section);
        return new ItemLootItem(item, amount, maxAmount, itemLootMeta, conditionalBonus, enchantmentBonus, smeltIfBurning);
    }

    public static String toSection(ItemStack itemStack) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("item: ").append(itemStack.getType().name().toLowerCase()).append('\n');
        stringBuilder.append("amount: ").append(itemStack.getAmount()).append('\n');

        ItemLootMeta.applyProperties(itemStack, stringBuilder);

        return stringBuilder.toString();
    }

    public static class ConditionalBonus {

        private final List<LootCondition> conditions;
        private final NumberProvider bonusPerCondition;

        public ConditionalBonus(List<LootCondition> conditions, NumberProvider bonusPerCondition) {
            this.conditions = conditions;
            this.bonusPerCondition = bonusPerCondition;
        }

        public int getBonusAmount(LootContext context) {
            return this.conditions.stream()
                    .filter(x -> x.check(context))
                    .mapToInt(x -> this.bonusPerCondition.getInteger())
                    .sum();
        }

    }

    public static class EnchantmentBonus {

        private final BonusFormula formula;
        private final Enchantment enchantment;
        private final NumberProvider bonus;
        private final NumberProvider probability;

        public EnchantmentBonus(BonusFormula formula, Enchantment enchantment, NumberProvider bonus, NumberProvider probability) {
            this.formula = formula;
            this.enchantment = enchantment;
            this.bonus = bonus;
            this.probability = probability;
        }

        public int getBonusAmount(LootContext context, int originalAmount) {
            ItemStack itemUsed = context.getItemUsed();
            if (itemUsed == null)
                return 0;

            int level = itemUsed.getEnchantmentLevel(this.enchantment);
            if (level <= 0)
                return 0;

            int bonus = 0;
            switch (this.formula) {
                case UNIFORM:
                    for (int i = 0; i < level; i++)
                        bonus += this.bonus.getInteger();
                    break;
                case BINOMIAL:
                    int n = level + this.bonus.getInteger();
                    double p = this.probability.getDouble();
                    for (int i = 0; i < n; i++)
                        if (LootUtils.checkChance(p))
                            bonus++;
                    break;
                case ORE_DROPS:
                    int multiplier = LootUtils.RANDOM.nextInt(level + 2) - 1;
                    if (multiplier > 0)
                        bonus += originalAmount * multiplier;
                    break;
            }
            return bonus;
        }

    }

    public enum BonusFormula {
        UNIFORM,
        BINOMIAL, // Requires an extra probability parameter
        ORE_DROPS;

        public static BonusFormula fromString(String name) {
            for (BonusFormula value : values())
                if (value.name().toLowerCase().equals(name))
                    return value;
            return UNIFORM;
        }
    }

}
