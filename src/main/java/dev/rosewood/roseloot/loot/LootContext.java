package dev.rosewood.roseloot.loot;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LootContext {

    private final LivingEntity looter;
    private final LivingEntity lootedEntity;
    private final Block lootedBlock;
    private final FishHook fishHook;

    public LootContext(@Nullable LivingEntity looter, @NotNull LivingEntity lootedEntity) {
        this.looter = looter;
        this.lootedEntity = lootedEntity;
        this.lootedBlock = null;
        this.fishHook = null;
    }

    public LootContext(@Nullable LivingEntity looter, @NotNull Block lootedBlock) {
        this.looter = looter;
        this.lootedBlock = lootedBlock;
        this.lootedEntity = null;
        this.fishHook = null;
    }

    public LootContext(@NotNull LivingEntity looter, @NotNull FishHook fishHook) {
        this.looter = looter;
        this.fishHook = fishHook;
        this.lootedEntity = null;
        this.lootedBlock = null;
    }

    /**
     * @return the entity that triggered the loot generation
     */
    @Nullable
    public LivingEntity getLooter() {
        return this.looter;
    }

    /**
     * @return the looted entity
     */
    @Nullable
    public LivingEntity getLootedEntity() {
        return this.lootedEntity;
    }

    /**
     * @return the looted block
     */
    @Nullable
    public Block getLootedBlock() {
        return this.lootedBlock;
    }

    /**
     * @return the fish hook
     */
    @Nullable
    public FishHook getFishHook() {
        return this.fishHook;
    }

    /**
     * @return the Location for this context
     */
    @NotNull
    public Location getLocation() {
        if (this.lootedEntity != null) return this.lootedEntity.getLocation();
        if (this.lootedBlock != null) return this.lootedBlock.getLocation();
        if (this.fishHook != null) return this.fishHook.getLocation();
        if (this.looter != null) return this.looter.getLocation();
        throw new IllegalStateException("LootContext does not have a Location");
    }

    /**
     * @return the luck level for this context, used for bonus rolls
     */
    public int getLuckLevel() {
        int luck = 0;
        if (this.looter != null) {
            AttributeInstance attribute = this.looter.getAttribute(Attribute.GENERIC_LUCK);
            if (attribute != null)
                luck += attribute.getValue();
        }

        if (this.fishHook != null) {
            ItemStack item = this.getItemUsed();
            if (item != null && item.getType() == Material.FISHING_ROD && item.getItemMeta() != null)
                luck += item.getItemMeta().getEnchantLevel(Enchantment.LUCK);
        }

        return luck;
    }

    /**
     * @return the ItemStack used for this context
     */
    @Nullable
    public ItemStack getItemUsed() {
        if (this.looter == null)
            return null;

        EntityEquipment equipment = this.looter.getEquipment();
        if (equipment == null)
            return null;

        return equipment.getItemInMainHand();
    }

}
