package dev.rosewood.roseloot.loot.item;

import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.hook.ItemsAdderHook;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ChangeToolDurabilityLootItem implements TriggerableLootItem {

    private final NumberProvider amount;
    private final boolean ignoreUnbreaking;

    private ChangeToolDurabilityLootItem(NumberProvider amount, boolean ignoreUnbreaking) {
        this.amount = amount;
        this.ignoreUnbreaking = ignoreUnbreaking;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        if (context.getLootingPlayer().map(Player::getGameMode).orElse(null) == GameMode.CREATIVE)
            return;

        Optional<ItemStack> itemUsedOptional = context.getItemUsed();
        if (itemUsedOptional.isEmpty())
            return;

        ItemStack itemStack = itemUsedOptional.get();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!(itemMeta instanceof Damageable damageable) || itemMeta.isUnbreakable())
            return;

        Player player = context.getLootingPlayer().orElse(null);
        if (this.applyDurability(player, itemStack, damageable, itemMeta.getEnchantLevel(Enchantment.DURABILITY), context)) {
            itemStack.setItemMeta(itemMeta);
            if (damageable.getDamage() >= itemStack.getType().getMaxDurability() - 1) {
                if (player != null)
                    LootUtils.playItemBreakAnimation(player, itemStack);
                itemStack.setAmount(0);
            }
        }
    }

    /**
     * Applies the durability change to the given item.
     *
     * @param player The player using the tool, may be null
     * @param itemStack The item having its durability changed
     * @param damageable The damageable item meta to change the durability of
     * @param unbreakingLevel The level of unbreaking on the item
     * @param context The LootContext
     * @return true if the ItemMeta needs to be applied, false otherwise
     */
    @SuppressWarnings("deprecation")
    private boolean applyDurability(Player player, ItemStack itemStack, Damageable damageable, int unbreakingLevel, LootContext context) {
        int originalDamage = -this.amount.getInteger(context);
        if (originalDamage == 0)
            return false;

        // Only allow repairing up to the tool's maximum durability to prevent issues with custom item plugins
        int currentDamage = ItemsAdderHook.getDamage(itemStack, damageable);
        if (currentDamage + originalDamage <= 0)
            originalDamage = -currentDamage;

        int actualDamage;
        if (originalDamage > 0 && !this.ignoreUnbreaking && unbreakingLevel > 0) {
            actualDamage = 0;
            int iterations = Math.min(originalDamage, 10000);
            for (int i = 0; i < iterations; i++)
                if (!LootUtils.shouldIgnoreDurabilityDecrease(unbreakingLevel))
                    actualDamage++;
        } else {
            actualDamage = originalDamage;
        }

        if (actualDamage == 0)
            return false;

        if (player != null) {
            PlayerItemDamageEvent event;
            if (NMSUtil.isPaper()) {
                event = new PlayerItemDamageEvent(player, itemStack, actualDamage, originalDamage);
            } else {
                event = new PlayerItemDamageEvent(player, itemStack, actualDamage);
            }

            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled())
                return false;

            actualDamage = event.getDamage();
        }

        damageable.setDamage(damageable.getDamage() + actualDamage);
        return true;
    }

    public static ChangeToolDurabilityLootItem fromSection(ConfigurationSection section) {
        NumberProvider amount = NumberProvider.fromSection(section, "amount", 0);
        boolean ignoreUnbreaking = section.getBoolean("ignore-unbreaking", false);
        return new ChangeToolDurabilityLootItem(amount, ignoreUnbreaking);
    }

}
