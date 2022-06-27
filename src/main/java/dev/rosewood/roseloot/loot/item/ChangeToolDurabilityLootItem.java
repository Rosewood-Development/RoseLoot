package dev.rosewood.roseloot.loot.item;

import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.hook.ItemsAdderHook;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.NumberProvider;
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

public class ChangeToolDurabilityLootItem implements TriggerableLootItem<ChangeToolDurabilityLootItem.DurabilityChangeInstance> {

    private final DurabilityChangeInstance durabilityChangeInstance;

    private ChangeToolDurabilityLootItem(DurabilityChangeInstance durabilityChangeInstance) {
        this.durabilityChangeInstance = durabilityChangeInstance;
    }

    @Override
    public DurabilityChangeInstance create(LootContext context) {
        return this.durabilityChangeInstance;
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
        if (damageable.getDamage() >= itemStack.getType().getMaxDurability() - 1) {
            itemStack.setAmount(0);
            if (player != null)
                LootUtils.playItemBreakAnimation(player, itemStack);
        }

        if (this.durabilityChangeInstance.applyDurability(player, itemStack, damageable, itemMeta.getEnchantLevel(Enchantment.DURABILITY)))
            itemStack.setItemMeta(itemMeta);
    }

    public static ChangeToolDurabilityLootItem fromSection(ConfigurationSection section) {
        NumberProvider amount = NumberProvider.fromSection(section, "amount", 0);
        boolean ignoreUnbreaking = section.getBoolean("ignore-unbreaking", false);
        return new ChangeToolDurabilityLootItem(new DurabilityChangeInstance(amount, ignoreUnbreaking));
    }

    public record DurabilityChangeInstance(NumberProvider amount, boolean ignoreUnbreaking) {

        /**
         * Applies the durability change to the given item.
         *
         * @param player The player using the tool, may be null
         * @param itemStack The item having its durability changed
         * @param damageable The damageable item meta to change the durability of
         * @param unbreakingLevel The level of unbreaking on the item
         * @return true if the ItemMeta needs to be applied, false otherwise
         */
        public boolean applyDurability(Player player, ItemStack itemStack, Damageable damageable, int unbreakingLevel) {
            int change = this.amount.getInteger();
            if (change == 0)
                return false;

            if (change < 0) {
                if (!this.ignoreUnbreaking || unbreakingLevel <= 0) {
                    // Limit the amount of change to prevent hanging the main thread calculating the unbreaking enchantment
                    change = Math.min(-change, 10000);
                    int totalDamage = 0;
                    for (int i = 0; i < change; i++)
                        if (!LootUtils.shouldIgnoreDurabilityDecrease(unbreakingLevel))
                            totalDamage++;

                    if (!ItemsAdderHook.offsetItemDurability(itemStack, -totalDamage)) {
                        if (player != null)
                            totalDamage = this.firePlayerItemDamageEvent(player, itemStack, totalDamage, totalDamage);
                        damageable.setDamage(damageable.getDamage() + totalDamage);
                        return true;
                    }
                } else {
                    change = -change;
                    if (!ItemsAdderHook.offsetItemDurability(itemStack, -change)) {
                        if (player != null)
                            change = -this.firePlayerItemDamageEvent(player, itemStack, change, change);
                        damageable.setDamage(damageable.getDamage() + change);
                        return true;
                    }
                }
            } else {
                if (!ItemsAdderHook.offsetItemDurability(itemStack, change)) {
                    damageable.setDamage(damageable.getDamage() - change);
                    return true;
                }
            }
            return false;
        }

        private int firePlayerItemDamageEvent(Player player, ItemStack itemStack, int damage, int originalDamage) {
            if (NMSUtil.isPaper()) {
                PlayerItemDamageEvent event = new PlayerItemDamageEvent(player, itemStack, damage, originalDamage);
                Bukkit.getPluginManager().callEvent(event);
                return event.isCancelled() ? 0 : event.getDamage();
            } else {
                @SuppressWarnings("deprecation")
                PlayerItemDamageEvent event = new PlayerItemDamageEvent(player, itemStack, damage);
                Bukkit.getPluginManager().callEvent(event);
                return event.isCancelled() ? 0 : event.getDamage();
            }
        }

    }

}
