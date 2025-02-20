package dev.rosewood.roseloot.util;

import dev.rosewood.roseloot.RoseLoot;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class VoucherUtils {

    private static final NamespacedKey VOUCHER_LOOTTABLE_KEY = new NamespacedKey(RoseLoot.getInstance(), "voucher_loottable");

    private VoucherUtils() {

    }

    /**
     * Sets the voucher data to the ItemStack
     *
     * @param itemStack The ItemStack to set the voucher data to
     * @param lootTable The loottable to execute when the voucher is used
     */
    public static void setVoucherData(ItemStack itemStack, String lootTable) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        pdc.set(VOUCHER_LOOTTABLE_KEY, PersistentDataType.STRING, lootTable);
        itemStack.setItemMeta(itemMeta);
    }

    /**
     * Gets the loottable of the voucher from the ItemStack
     *
     * @param itemStack The ItemStack to get the loottable from
     * @return The loottable of the voucher, or null if this ItemStack is not a voucher
     */
    public static String getVoucherLootTableName(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return null;

        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        return pdc.get(VOUCHER_LOOTTABLE_KEY, PersistentDataType.STRING);
    }

}
