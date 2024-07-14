package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.hook.NBTAPIHook;
import dev.rosewood.roseloot.hook.items.CustomItemPlugin;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.LootConditionParser;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.item.meta.ItemLootMeta;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.provider.StringProvider;
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

    public CustomItemLootItem(CustomItemPlugin customItemPlugin, ItemLootItem base) {
        super(base.item, base.amount, base.maxAmount, base.amountModifiers, base.itemLootMeta, base.enchantmentBonus, base.smeltIfBurning, base.nbt);
        this.customItemPlugin = customItemPlugin;
    }

    private ItemStack resolveItem(LootContext context) {
        String itemId = this.item.get(context);
        ItemStack itemStack = this.customItemPlugin.resolveItem(context, itemId);
        if (itemStack == null) {
            failToResolve(itemId, this.customItemPlugin);
            return null;
        }
        return itemStack;
    }

    public static CustomItemLootItem fromSection(ConfigurationSection section) {
        String plugin = section.getString("plugin");
        CustomItemPlugin customItemPlugin = CustomItemPlugin.fromString(plugin);
        if (customItemPlugin == null)
            return null;

        ItemLootItem base = ItemLootItem.fromSection(section);
        if (base == null)
            return null;

        return new CustomItemLootItem(customItemPlugin, base);
    }

    protected static void failToResolve(String itemId, CustomItemPlugin customItemPlugin) {
        RoseLoot.getInstance().getLogger().warning("Failed to resolve item [" + itemId + "] from [" + customItemPlugin.name().toLowerCase() + "]");
    }

}
