package dev.rosewood.roseloot.loot.item;

import com.willfp.eco.core.items.Items;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.manager.LootConditionManager;
import dev.rosewood.roseloot.util.EnchantingUtils;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.NumberProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EcoLootItem implements LootItem<List<ItemStack>> {
    private final String lookup;
    private final NumberProvider amount;
    private final NumberProvider maxAmount;
    private final List<AmountModifier> amountModifiers;
    private final EnchantmentBonus enchantmentBonus;

    public EcoLootItem(String lookup, NumberProvider amount, NumberProvider maxAmount, List<AmountModifier> amountModifiers, EnchantmentBonus enchantmentBonus) {
        this.lookup = lookup;
        this.amount = amount;
        this.maxAmount = maxAmount;
        this.amountModifiers = amountModifiers;
        this.enchantmentBonus = enchantmentBonus;
    }

    @Override
    public List<ItemStack> create(LootContext context) {
        ItemStack item = Items.lookup(this.lookup).getItem();
        List<ItemStack> generatedItems = new ArrayList<>();

        int amount = this.amount.getInteger();

        for (AmountModifier amountModifier : this.amountModifiers) {
            if (!amountModifier.check(context))
                break;

            if (amountModifier.isAdditive()) {
                amount += amountModifier.getValue();
            } else {
                amount = amountModifier.getValue();
            }
        }

        if (this.enchantmentBonus != null)
            amount += this.enchantmentBonus.getBonusAmount(context, amount);

        amount = Math.min(amount, this.maxAmount.getInteger());

        if (amount > 0) {
            int maxStackSize = item.getMaxStackSize();
            while (amount > maxStackSize) {
                amount -= maxStackSize;
                ItemStack clone = item.clone();
                clone.setAmount(maxStackSize);
                generatedItems.add(clone);
            }

            if (amount > 0) {
                ItemStack clone = item.clone();
                clone.setAmount(amount);
                generatedItems.add(clone);
            }
        }

        return generatedItems;
    }

    public static EcoLootItem fromSection(ConfigurationSection section) {
        if (!section.contains("item"))
            return null;

        NumberProvider amount = NumberProvider.fromSection(section, "amount", 1);
        NumberProvider maxAmount = NumberProvider.fromSection(section, "max-amount", Integer.MAX_VALUE);

        List<AmountModifier> amountModifiers = new ArrayList<>();
        ConfigurationSection amountModifiersSection = section.getConfigurationSection("amount-modifiers");
        if (amountModifiersSection != null) {
            LootConditionManager lootConditionManager = RoseLoot.getInstance().getManager(LootConditionManager.class);
            for (String key : amountModifiersSection.getKeys(false)) {
                ConfigurationSection entrySection = amountModifiersSection.getConfigurationSection(key);
                if (entrySection != null) {
                    List<LootCondition> conditions = new ArrayList<>();
                    for (String conditionString : entrySection.getStringList("conditions")) {
                        LootCondition condition = lootConditionManager.parse(conditionString);
                        if (condition != null)
                            conditions.add(condition);
                    }

                    NumberProvider value = NumberProvider.fromSection(entrySection, "value", 1);
                    boolean add = entrySection.getBoolean("add", false);
                    amountModifiers.add(new AmountModifier(conditions, value, add));
                }
            }
        }

        ConfigurationSection enchantmentBonusSection = section.getConfigurationSection("enchantment-bonus");
        EnchantmentBonus enchantmentBonus = null;
        if (enchantmentBonusSection != null) {
            EnchantmentBonus.BonusFormula formula = EnchantmentBonus.BonusFormula.fromString(enchantmentBonusSection.getString("formula", EnchantmentBonus.BonusFormula.UNIFORM.name()));
            String enchantmentString = enchantmentBonusSection.getString("enchantment");
            if (enchantmentString != null) {
                Enchantment enchantment = EnchantingUtils.getEnchantmentByName(enchantmentString);
                NumberProvider bonusPerLevel = NumberProvider.fromSection(enchantmentBonusSection, "bonus-per-level", 0);
                NumberProvider probability = NumberProvider.fromSection(enchantmentBonusSection, "probability", 0);
                if (enchantment != null)
                    enchantmentBonus = new EnchantmentBonus(formula, enchantment, bonusPerLevel, probability);
            }
        }

        return new EcoLootItem(section.getString("item"), amount, maxAmount, amountModifiers, enchantmentBonus);
    }

    public static class AmountModifier {

        private final List<LootCondition> conditions;
        private final NumberProvider value;
        private final boolean add;

        public AmountModifier(List<LootCondition> conditions, NumberProvider value, boolean add) {
            this.conditions = conditions;
            this.value = value;
            this.add = add;
        }

        public boolean check(LootContext context) {
            return this.conditions.stream().allMatch(x -> x.check(context));
        }

        public int getValue() {
            return this.value.getInteger();
        }

        public boolean isAdditive() {
            return this.add;
        }

    }

    public static class EnchantmentBonus {

        private final EnchantmentBonus.BonusFormula formula;
        private final Enchantment enchantment;
        private final NumberProvider bonus;
        private final NumberProvider probability;

        public EnchantmentBonus(EnchantmentBonus.BonusFormula formula, Enchantment enchantment, NumberProvider bonus, NumberProvider probability) {
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
                    bonus += LootUtils.randomInRange(0, this.bonus.getInteger() * level);
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
}
