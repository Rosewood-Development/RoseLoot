package dev.rosewood.roseloot.loot.item;

import com.google.common.collect.Iterators;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
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

    public TagLootItem(Tag<Material> tag, NumberProvider amount, NumberProvider maxAmount, ItemLootMeta itemLootMeta, ConditionalBonus conditionalBonus, EnchantmentBonus enchantmentBonus, boolean smeltIfBurning) {
        super(null, amount, maxAmount, itemLootMeta, conditionalBonus, enchantmentBonus, smeltIfBurning);
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

        NamespacedKey key = NamespacedKey.fromString(tagString);
        if (key == null)
            return null;

        // Look for matching tags
        Tag<Material> tag = Bukkit.getTag(Tag.REGISTRY_ITEMS, key, Material.class);
        if (tag == null)
            tag = Bukkit.getTag(Tag.REGISTRY_BLOCKS, key, Material.class);

        if (tag == null)
            return null;

        NumberProvider amount = NumberProvider.fromSection(section, "amount", 1);
        NumberProvider maxAmount = NumberProvider.fromSection(section, "max-amount", Integer.MAX_VALUE);

        ConfigurationSection conditionBonusSection = section.getConfigurationSection("conditional-bonus");
        TagLootItem.ConditionalBonus conditionalBonus = null;
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
        ItemLootMeta itemLootMeta = ItemLootMeta.fromSection(Iterators.get(tag.getValues().iterator(), 0), section);
        return new TagLootItem(tag, amount, maxAmount, itemLootMeta, conditionalBonus, enchantmentBonus, smeltIfBurning);
    }

    public static String toSection(ItemStack itemStack) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("tag: ").append(itemStack.getType().name().toLowerCase()).append('\n');
        stringBuilder.append("amount: ").append(itemStack.getAmount()).append('\n');

        ItemLootMeta.applyProperties(itemStack, stringBuilder);

        return stringBuilder.toString();
    }

}
