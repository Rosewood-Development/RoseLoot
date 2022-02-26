package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.hook.items.EcoItemProvider;
import dev.rosewood.roseloot.hook.items.ExecutableItemProvider;
import dev.rosewood.roseloot.hook.items.ItemBridgeItemProvider;
import dev.rosewood.roseloot.hook.items.ItemProvider;
import dev.rosewood.roseloot.hook.items.ItemsAdderItemProvider;
import dev.rosewood.roseloot.hook.items.ItemsXLItemProvider;
import dev.rosewood.roseloot.hook.items.KnokkoCustomItemProvider;
import dev.rosewood.roseloot.hook.items.MMOItemProvider;
import dev.rosewood.roseloot.hook.items.OraxenItemProvider;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.manager.LootConditionManager;
import dev.rosewood.roseloot.util.EnchantingUtils;
import dev.rosewood.roseloot.util.NumberProvider;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class CustomItemLootItem extends ItemLootItem {

    private final ItemStack itemStack;

    public CustomItemLootItem(ItemStack itemStack, NumberProvider amount, NumberProvider maxAmount, List<AmountModifier> amountModifiers, EnchantmentBonus enchantmentBonus) {
        super(itemStack.getType(), amount, maxAmount, amountModifiers, null, enchantmentBonus, false);
        this.itemStack = itemStack;
    }

    protected ItemStack getCreationItem(LootContext context) {
        return this.itemStack;
    }

    public static CustomItemLootItem fromSection(ConfigurationSection section) {
        String plugin = section.getString("plugin");
        String itemId = section.getString("item");

        CustomItemPlugin customItemPlugin = CustomItemPlugin.fromString(plugin);
        if (customItemPlugin == null)
            return null;

        if (itemId == null)
            return null;

        ItemStack itemStack = customItemPlugin.resolveItem(itemId);
        if (itemStack == null || itemStack.getType() == Material.AIR)
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

        return new CustomItemLootItem(itemStack, amount, maxAmount, amountModifiers, enchantmentBonus);
    }

    public enum CustomItemPlugin {
        ECOITEMS(new EcoItemProvider()),
        MMOITEMS(new MMOItemProvider()),
        ITEMBRIDGE(new ItemBridgeItemProvider()),
        EXECUTABLEITEMS(new ExecutableItemProvider()),
        ITEMSADDER(new ItemsAdderItemProvider()),
        ITEMSXL(new ItemsXLItemProvider()),
        ORAXEN(new OraxenItemProvider()),
        KNOKKOCUSTOMITEMS(new KnokkoCustomItemProvider());

        private final ItemProvider itemProvider;

        CustomItemPlugin(ItemProvider itemProvider) {
            this.itemProvider = itemProvider;
        }

        public ItemStack resolveItem(String id) {
            return this.itemProvider.getItem(id);
        }

        public static CustomItemPlugin fromString(String name) {
            for (CustomItemPlugin value : values())
                if (value.name().equalsIgnoreCase(name))
                    return value;
            return null;
        }
    }

}
