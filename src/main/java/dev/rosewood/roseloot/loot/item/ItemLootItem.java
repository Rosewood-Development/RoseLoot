package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.hook.NBTAPIHook;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.item.meta.ItemLootMeta;
import dev.rosewood.roseloot.manager.LootConditionManager;
import dev.rosewood.roseloot.util.EnchantingUtils;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.NumberProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class ItemLootItem implements ItemGenerativeLootItem {

    protected Material item;
    protected final ItemLootMeta itemLootMeta;
    protected final NumberProvider amount;
    protected final NumberProvider maxAmount;
    protected final List<AmountModifier> amountModifiers;
    protected final EnchantmentBonus enchantmentBonus;
    protected final boolean smeltIfBurning;
    protected final String nbt;

    public ItemLootItem(Material item, NumberProvider amount, NumberProvider maxAmount, List<AmountModifier> amountModifiers, ItemLootMeta itemLootMeta, EnchantmentBonus enchantmentBonus, boolean smeltIfBurning, String nbt) {
        this.item = item;
        this.amount = amount;
        this.maxAmount = maxAmount;
        this.amountModifiers = amountModifiers;
        this.itemLootMeta = itemLootMeta;
        this.enchantmentBonus = enchantmentBonus;
        this.smeltIfBurning = smeltIfBurning;
        this.nbt = nbt;
    }

    protected ItemLootItem() {
        this(null, null, null, null, null, null, false, null);
    }

    protected ItemStack getCreationItem(LootContext context) {
        Material item = this.item;
        Optional<LivingEntity> lootedEntity = context.get(LootContextParams.LOOTED_ENTITY);
        if (this.smeltIfBurning && lootedEntity.isPresent() && lootedEntity.get().getFireTicks() > 0) {
            Iterator<Recipe> recipesIterator = Bukkit.recipeIterator();
            while (recipesIterator.hasNext()) {
                Recipe recipe = recipesIterator.next();
                if (recipe instanceof FurnaceRecipe furnaceRecipe && furnaceRecipe.getInput().getType() == item) {
                    item = furnaceRecipe.getResult().getType();
                    break;
                }
            }
        }

        ItemStack itemStack = this.itemLootMeta.apply(new ItemStack(item), context);
        if (this.nbt != null && !this.nbt.isEmpty())
            NBTAPIHook.mergeItemNBT(itemStack, this.nbt);

        return itemStack;
    }

    @Override
    public List<ItemStack> generate(LootContext context) {
        List<ItemStack> generatedItems = new ArrayList<>();

        int amount = this.amount.getInteger(context);

        for (AmountModifier amountModifier : this.amountModifiers) {
            if (!amountModifier.check(context))
                break;

            if (amountModifier.additive()) {
                amount += amountModifier.getValue(context);
            } else {
                amount = amountModifier.getValue(context);
            }
        }

        if (this.enchantmentBonus != null)
            amount += this.enchantmentBonus.getBonusAmount(context, amount);
        amount = Math.min(amount, this.maxAmount.getInteger(context));

        ItemStack creationItem = this.getCreationItem(context);
        if (creationItem != null && amount > 0) {
            int maxStackSize = creationItem.getMaxStackSize();
            while (amount > maxStackSize) {
                amount -= maxStackSize;
                ItemStack clone = creationItem.clone();
                clone.setAmount(maxStackSize);
                generatedItems.add(clone);
            }

            if (amount > 0) {
                ItemStack clone = creationItem.clone();
                clone.setAmount(amount);
                generatedItems.add(clone);
            }
        }

        context.getPlaceholders().add("item_amount", generatedItems.stream().mapToInt(ItemStack::getAmount).sum());

        return generatedItems;
    }

    @Override
    public List<ItemStack> getAllItems(LootContext context) {
        List<ItemStack> generatedItems = new ArrayList<>();

        int amount = Math.min(this.amount.getInteger(context), this.maxAmount.getInteger(context));

        ItemStack creationItem = this.getCreationItem(context);
        if (creationItem != null && amount > 0) {
            int maxStackSize = creationItem.getMaxStackSize();
            while (amount > maxStackSize) {
                amount -= maxStackSize;
                ItemStack clone = creationItem.clone();
                clone.setAmount(maxStackSize);
                generatedItems.add(clone);
            }

            if (amount > 0) {
                ItemStack clone = creationItem.clone();
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
        String nbt = section.getString("nbt");
        ItemLootMeta itemLootMeta = ItemLootMeta.fromSection(item, section);
        return new ItemLootItem(item, amount, maxAmount, amountModifiers, itemLootMeta, enchantmentBonus, smeltIfBurning, nbt);
    }

    public static String toSection(ItemStack itemStack, boolean keepVanillaNBT) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("item: ").append(itemStack.getType().name().toLowerCase()).append('\n');
        stringBuilder.append("amount: ").append(itemStack.getAmount()).append('\n');

        ItemLootMeta.applyProperties(itemStack, stringBuilder);

        if (Bukkit.getPluginManager().isPluginEnabled("NBTAPI")) {
            String customNBT = NBTAPIHook.getCustomNBTString(itemStack, keepVanillaNBT);
            if (customNBT != null && customNBT.trim().length() > 2)
                stringBuilder.append("nbt: '").append(customNBT.replaceAll(Pattern.quote("'"), "''")).append("'").append('\n');
        }

        return stringBuilder.toString();
    }

    public record AmountModifier(List<LootCondition> conditions, NumberProvider value, boolean additive) {

        public boolean check(LootContext context) {
            return this.conditions.stream().allMatch(x -> x.check(context));
        }

        public int getValue(LootContext context) {
            return this.value.getInteger(context);
        }

    }

    public record EnchantmentBonus(BonusFormula formula, Enchantment enchantment, NumberProvider bonus, NumberProvider probability) {

        public int getBonusAmount(LootContext context, int originalAmount) {
            int level = context.getEnchantmentLevel(this.enchantment);
            if (level <= 0)
                return 0;

            int bonus = 0;
            switch (this.formula) {
                case UNIFORM -> bonus += LootUtils.randomInRange(0, this.bonus.getInteger(context) * level);
                case BINOMIAL -> {
                    int n = level + this.bonus.getInteger(context);
                    double p = this.probability.getDouble(context);
                    for (int i = 0; i < n; i++)
                        if (LootUtils.checkChance(p))
                            bonus++;
                }
                case ORE_DROPS -> {
                    int multiplier = LootUtils.RANDOM.nextInt(level + 2) - 1;
                    if (multiplier > 0)
                        bonus += originalAmount * multiplier;
                }
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
