package dev.rosewood.roseloot.loot.item;

import com.google.common.collect.Iterators;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.meta.ItemLootMeta;
import dev.rosewood.roseloot.manager.LootConditionManager;
import dev.rosewood.roseloot.util.EnchantingUtils;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.NumberProvider;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class TagLootItem extends ItemLootItem {

    private final Tag<Material> tag;

    public TagLootItem(Tag<Material> tag, NumberProvider amount, NumberProvider maxAmount, List<AmountModifier> amountModifiers, ItemLootMeta itemLootMeta, EnchantmentBonus enchantmentBonus, boolean smeltIfBurning, String nbt) {
        super(null, amount, maxAmount, amountModifiers, itemLootMeta, enchantmentBonus, smeltIfBurning, nbt);
        this.tag = tag;
    }

    @Override
    public List<ItemStack> create(LootContext context) {
        List<Material> values = new ArrayList<>(this.tag.getValues());
        this.item = values.get(LootUtils.RANDOM.nextInt(values.size()));
        return super.create(context);
    }

    public static TagLootItem fromSection(ConfigurationSection section) {
        String tagString = section.getString("tag");
        if (tagString == null)
            return null;

        NamespacedKey namespacedKey = NamespacedKey.fromString(tagString);
        if (namespacedKey == null)
            return null;

        // Look for matching tags
        Tag<Material> tag = Bukkit.getTag(Tag.REGISTRY_ITEMS, namespacedKey, Material.class);
        if (tag == null)
            tag = Bukkit.getTag(Tag.REGISTRY_BLOCKS, namespacedKey, Material.class);

        if (tag == null)
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
        TagLootItem.EnchantmentBonus enchantmentBonus = null;
        if (enchantmentBonusSection != null) {
            BonusFormula formula = BonusFormula.fromString(enchantmentBonusSection.getString("formula", BonusFormula.UNIFORM.name()));
            String enchantmentString = enchantmentBonusSection.getString("enchantment");
            if (enchantmentString != null) {
                Enchantment enchantment = EnchantingUtils.getEnchantmentByName(enchantmentString);
                NumberProvider bonusPerLevel = NumberProvider.fromSection(enchantmentBonusSection, "bonus-per-level", 0);
                NumberProvider probability = NumberProvider.fromSection(enchantmentBonusSection, "probability", 0);
                if (enchantment != null)
                    enchantmentBonus = new TagLootItem.EnchantmentBonus(formula, enchantment, bonusPerLevel, probability);
            }
        }

        boolean smeltIfBurning = section.getBoolean("smelt-if-burning", false);
        String nbt = section.getString("nbt");
        ItemLootMeta itemLootMeta = ItemLootMeta.fromSection(Iterators.get(tag.getValues().iterator(), 0), section);
        return new TagLootItem(tag, amount, maxAmount, amountModifiers, itemLootMeta, enchantmentBonus, smeltIfBurning, nbt);
    }

}
