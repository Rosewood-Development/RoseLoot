package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.hook.NBTAPIHook;
import dev.rosewood.roseloot.hook.items.CustomItemPlugin;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.item.meta.ItemLootMeta;
import dev.rosewood.roseloot.manager.LootConditionManager;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.util.nms.EnchantingUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class CustomItemLootItem extends ItemLootItem {

    private final CustomItemPlugin customItemPlugin;
    private final String itemId;

    public CustomItemLootItem(CustomItemPlugin customItemPlugin, String itemId, NumberProvider amount, NumberProvider maxAmount, List<AmountModifier> amountModifiers, ItemLootMeta itemLootMeta, EnchantmentBonus enchantmentBonus, boolean smeltIfBurning, String nbt) {
        super(null, amount, maxAmount, amountModifiers, itemLootMeta, enchantmentBonus, smeltIfBurning, nbt);
        this.customItemPlugin = customItemPlugin;
        this.itemId = itemId;
    }

    private ItemStack resolveItem(LootContext context) {
        ItemStack itemStack = this.customItemPlugin.resolveItem(context, this.itemId);
        if (itemStack == null) {
            RoseLoot.getInstance().getLogger().warning("Failed to resolve item [" + this.itemId + "] from [" + this.customItemPlugin.name().toLowerCase() + "]");
            return null;
        }
        return itemStack;
    }

    protected ItemStack getCreationItem(LootContext context) {
        ItemStack itemStack = this.resolveItem(context);
        if (itemStack == null)
            return null;

        Optional<LivingEntity> lootedEntity = context.get(LootContextParams.LOOTED_ENTITY);
        if (this.smeltIfBurning && lootedEntity.isPresent() && lootedEntity.get().getFireTicks() > 0) {
            Iterator<Recipe> recipesIterator = Bukkit.recipeIterator();
            while (recipesIterator.hasNext()) {
                Recipe recipe = recipesIterator.next();
                if (recipe instanceof FurnaceRecipe furnaceRecipe && furnaceRecipe.getInput().getType() == itemStack.getType()) {
                    itemStack.setType(furnaceRecipe.getResult().getType());
                    break;
                }
            }
        }

        if (this.itemLootMeta != null)
            this.itemLootMeta.apply(itemStack, context);

        if (this.nbt != null && !this.nbt.isEmpty())
            NBTAPIHook.mergeItemNBT(itemStack, this.nbt);

        return itemStack;
    }

    public static CustomItemLootItem fromSection(ConfigurationSection section) {
        String plugin = section.getString("plugin");
        String itemId = section.getString("item");

        CustomItemPlugin customItemPlugin = CustomItemPlugin.fromString(plugin);
        if (customItemPlugin == null)
            return null;

        if (itemId == null)
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
            BonusFormula formula = BonusFormula.fromString(enchantmentBonusSection.getString("formula", BonusFormula.UNIFORM.name()));
            String enchantmentString = enchantmentBonusSection.getString("enchantment");
            if (enchantmentString != null) {
                Enchantment enchantment = EnchantingUtils.getEnchantmentByName(enchantmentString);
                NumberProvider bonusPerLevel = NumberProvider.fromSection(enchantmentBonusSection, "bonus-per-level", 0);
                NumberProvider probability = NumberProvider.fromSection(enchantmentBonusSection, "probability", 0);
                if (enchantment != null)
                    enchantmentBonus = new EnchantmentBonus(formula, enchantment, bonusPerLevel, probability);
            }
        }

        boolean smeltIfBurning = section.getBoolean("smelt-if-burning", false);
        String nbt = section.getString("nbt");

        ItemStack itemStack = customItemPlugin.resolveItem(LootContext.builder().build(), itemId);
        ItemLootMeta itemLootMeta = itemStack == null ? null : ItemLootMeta.fromSection(itemStack.getType(), section);

        return new CustomItemLootItem(customItemPlugin, itemId, amount, maxAmount, amountModifiers, itemLootMeta, enchantmentBonus, smeltIfBurning, nbt);
    }

}
