package dev.rosewood.roseloot.loot.item;

import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.hook.NBTAPIHook;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.LootConditionParser;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.item.meta.ItemLootMeta;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.provider.StringProvider;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.VersionUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class ItemLootItem implements ItemGenerativeLootItem {

    protected final StringProvider item;
    protected final NumberProvider amount;
    protected final NumberProvider maxAmount;
    protected final List<AmountModifier> amountModifiers;
    protected final EnchantmentBonus enchantmentBonus;
    protected final boolean smeltIfBurning;
    protected final StringProvider nbt;
    private final Function<Material, ItemLootMeta> lootMetaFactoryFunction;
    private final Map<Material, ItemLootMeta> itemLootMetaMap;
    private boolean firstPlaceholderParseFailure;

    private ItemLootItem(StringProvider item, NumberProvider amount, NumberProvider maxAmount, List<AmountModifier> amountModifiers, EnchantmentBonus enchantmentBonus, boolean smeltIfBurning, StringProvider nbt, Function<Material, ItemLootMeta> lootMetaFactoryFunction, boolean tryResolve) {
        this.item = item;
        this.amount = amount;
        this.maxAmount = maxAmount;
        this.amountModifiers = amountModifiers;
        this.enchantmentBonus = enchantmentBonus;
        this.smeltIfBurning = smeltIfBurning;
        this.nbt = nbt;
        this.lootMetaFactoryFunction = lootMetaFactoryFunction;
        this.itemLootMetaMap = new ConcurrentHashMap<>();
        this.firstPlaceholderParseFailure = true;
        if (tryResolve)
            this.resolveItem(LootContext.none());
    }

    protected ItemLootItem(ItemLootItem base) {
        this(base.item, base.amount, base.maxAmount, base.amountModifiers, base.enchantmentBonus, base.smeltIfBurning, base.nbt, base.lootMetaFactoryFunction, false);
    }

    protected Optional<ItemStack> resolveItem(LootContext context) {
        String itemId = this.item.get(context);
        Material material = Material.matchMaterial(itemId);
        if (material == null)
            this.logFailToResolveMessage(itemId);
        return Optional.ofNullable(material).map(ItemStack::new);
    }

    protected void logFailToResolveMessage(String itemId) {
        // Only log for placeholders if this isn't the first time
        if (!itemId.startsWith("%") && !itemId.endsWith("%") || !this.firstPlaceholderParseFailure)
            RoseLoot.getInstance().getLogger().warning(this.getFailToResolveMessage(itemId));
        this.firstPlaceholderParseFailure = false;
    }

    protected String getFailToResolveMessage(String itemId) {
        return "Failed to resolve item [" + itemId + "]";
    }

    private Optional<ItemStack> getCreationItem(LootContext context) {
        return this.resolveItem(context).map(item -> {
            Optional<LivingEntity> lootedEntity = context.get(LootContextParams.LOOTED_ENTITY);
            if (this.smeltIfBurning && lootedEntity.isPresent() && lootedEntity.get().getFireTicks() > 0) {
                Iterator<Recipe> recipesIterator = Bukkit.recipeIterator();
                while (recipesIterator.hasNext()) {
                    Recipe recipe = recipesIterator.next();
                    if (recipe instanceof CookingRecipe<?> cookingRecipe) {
                        if (NMSUtil.isPaper()) {
                            if (cookingRecipe.getInputChoice().test(item)) {
                                item = cookingRecipe.getResult();
                                break;
                            }
                        } else {
                            if (cookingRecipe.getInput().getType() == item.getType()) {
                                item = cookingRecipe.getResult();
                                break;
                            }
                        }
                    }
                }
            }

            ItemLootMeta itemLootMeta = this.itemLootMetaMap.computeIfAbsent(item.getType(), this.lootMetaFactoryFunction);
            item = itemLootMeta.apply(item, context);
            if (this.nbt != null) {
                String nbt = this.nbt.get(context);
                NBTAPIHook.mergeItemNBT(item, nbt);
            }

            return item;
        });
    }

    @Override
    public List<ItemStack> generate(LootContext context) {
        int amount = this.amount.getInteger(context);

        for (AmountModifier amountModifier : this.amountModifiers) {
            if (!amountModifier.check(context))
                continue;

            if (amountModifier.additive()) {
                amount += amountModifier.getValue(context);
            } else {
                amount = amountModifier.getValue(context);
            }
        }

        if (this.enchantmentBonus != null)
            amount += this.enchantmentBonus.getBonusAmount(context, amount);
        amount = Math.min(amount, this.maxAmount.getInteger(context));

        int finalAmount = amount;
        return this.getCreationItem(context).map(item -> {
            List<ItemStack> generatedItems = new ArrayList<>(LootUtils.createItemStackCopies(item, finalAmount));
            context.addPlaceholder("item_amount", generatedItems.stream().mapToInt(ItemStack::getAmount).sum());
            return generatedItems;
        }).orElse(List.of());
    }

    @Override
    public List<ItemStack> getAllItems(LootContext context) {
        int amount = Math.min(this.amount.getInteger(context), this.maxAmount.getInteger(context));
        return this.getCreationItem(context)
                .map(item -> LootUtils.createItemStackCopies(item, amount))
                .orElse(List.of());
    }

    public static ItemLootItem fromSection(ConfigurationSection section) {
        return fromSection(section, "item", true);
    }

    public static ItemLootItem fromSection(ConfigurationSection section, String itemPropertyName, boolean resolveItem) {
        StringProvider item = StringProvider.fromSection(section, itemPropertyName, null);
        if (item == null)
            return null;

        NumberProvider amount = NumberProvider.fromSection(section, "amount", 1);
        NumberProvider maxAmount = NumberProvider.fromSection(section, "max-amount", Integer.MAX_VALUE);

        List<AmountModifier> amountModifiers = new ArrayList<>();
        ConfigurationSection amountModifiersSection = section.getConfigurationSection("amount-modifiers");
        if (amountModifiersSection != null) {
            for (String key : amountModifiersSection.getKeys(false)) {
                ConfigurationSection entrySection = amountModifiersSection.getConfigurationSection(key);
                if (entrySection != null) {
                    List<LootCondition> conditions = new ArrayList<>();
                    for (String conditionString : entrySection.getStringList("conditions")) {
                        LootCondition condition = LootConditionParser.parse(conditionString);
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
            BonusFormula formula = BonusFormula.fromString(enchantmentBonusSection.getString("formula", BonusFormula.UNIFORM.name()));
            String enchantmentString = enchantmentBonusSection.getString("enchantment");
            if (enchantmentString != null) {
                Enchantment enchantment = VersionUtils.getEnchantment(enchantmentString);
                NumberProvider bonusPerLevel = NumberProvider.fromSection(enchantmentBonusSection, "bonus-per-level", 0);
                NumberProvider probability = NumberProvider.fromSection(enchantmentBonusSection, "probability", 0);
                if (enchantment != null)
                    enchantmentBonus = new EnchantmentBonus(formula, enchantment, bonusPerLevel, probability);
            }
        }

        boolean smeltIfBurning = section.getBoolean("smelt-if-burning", false);
        StringProvider nbt = StringProvider.fromSection(section, "nbt", null);
        Function<Material, ItemLootMeta> lootMetaFactory = material -> ItemLootMeta.fromSection(material, section);
        return new ItemLootItem(item, amount, maxAmount, amountModifiers, enchantmentBonus, smeltIfBurning, nbt, lootMetaFactory, resolveItem);
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
