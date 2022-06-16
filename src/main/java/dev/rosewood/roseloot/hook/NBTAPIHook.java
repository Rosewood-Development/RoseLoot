package dev.rosewood.roseloot.hook;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class NBTAPIHook {

    private static final List<String> VANILLA_ITEM_NBT_KEYS = List.of(
            "id", "Count", "Damage", "Enchantments", "EntityTag", "display", "AttributeModifiers", "Unbreakable",
            "SkullOwner", "HideFlags", "author", "pages", "title", "generation", "Fireworks", "Flight", "RepairCost",
            "Variant", "BlockEntityTag", "Items", "tag", "StoredEnchantments", "Explosion", "Type", "Recipes",
            "Potion", "CustomPotionEffects", "CustomPotionColor"
    );
    private static Boolean enabled;

    public static boolean isEnabled() {
        if (enabled != null)
            return enabled;
        return enabled = Bukkit.getPluginManager().isPluginEnabled("NBTAPI");
    }

    /**
     * Merges the NBT of the given item with the given NBT string
     *
     * @param itemStack The ItemStack to merge the NBT with
     * @param nbt The NBT string to merge with the ItemStack
     */
    public static void mergeItemNBT(ItemStack itemStack, String nbt) {
        if (!isEnabled())
            return;

        NBTItem nbtItem = new NBTItem(itemStack);
        NBTCompound nbtCompound = new NBTContainer(nbt);
        nbtItem.mergeCompound(nbtCompound);
        nbtItem.applyNBT(itemStack);
    }

    /**
     * Gets all non-vanilla NBT from the given ItemStack
     *
     * @param itemStack The ItemStack to get the NBT from
     * @param keepVanillaNBT Whether to keep vanilla NBT or not
     * @return The NBT string of the given ItemStack
     */
    public static String getCustomNBTString(ItemStack itemStack, boolean keepVanillaNBT) {
        if (!isEnabled())
            return null;

        NBTItem nbtItem = new NBTItem(itemStack);
        if (!keepVanillaNBT)
            VANILLA_ITEM_NBT_KEYS.forEach(nbtItem::removeKey);

        return nbtItem.toString();
    }

}
