package dev.rosewood.roseloot.loot;

import dev.rosewood.roseloot.util.LootUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LootContext {

    private final Entity looter;
    private final LivingEntity lootedEntity;
    private final Block lootedBlock;
    private final FishHook fishHook;
    private final ItemStack inputItem;
    private final NamespacedKey vanillaLootTableKey, advancementKey;
    private final ExplosionType explosionType;
    private final boolean hasExistingItems;
    private final LootPlaceholders placeholders;

    private LootContext(Entity looter, LivingEntity lootedEntity, Block lootedBlock, FishHook fishHook, ItemStack inputItem, NamespacedKey vanillaLootTableKey, NamespacedKey advancementKey, ExplosionType explosionType, boolean hasExistingItems) {
        this.looter = looter;
        this.lootedEntity = lootedEntity;
        this.lootedBlock = lootedBlock;
        this.fishHook = fishHook;
        this.inputItem = inputItem;
        this.vanillaLootTableKey = vanillaLootTableKey;
        this.advancementKey = advancementKey;
        this.explosionType = explosionType;
        this.hasExistingItems = hasExistingItems;

        this.placeholders = new LootPlaceholders();
        this.addContextPlaceholders();
    }

    /**
     * @return the entity that triggered the loot generation
     */
    @Nullable
    public Entity getLooter() {
        return this.looter;
    }

    /**
     * @return the Player that ultimately caused the loot generation, may not be the same as {@link LootContext#getLooter()}
     */
    @Nullable
    public Player getLootingPlayer() {
        if (this.lootedEntity == null)
            return this.getLooter() instanceof Player ? (Player) this.getLooter() : null;
        return this.lootedEntity.getKiller();
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
     * @return the fishhook
     */
    @Nullable
    public FishHook getFishHook() {
        return this.fishHook;
    }

    /**
     * Gets the item primarily used for the loot generation.
     * For piglin bartering, it will be the bartered item.
     * For entity item drops, it will be the dropped item.
     *
     * @return the item primarily used for the loot generation
     */
    @Nullable
    public ItemStack getInputItem() {
        return this.inputItem;
    }

    /**
     * @return the NamespacedKey of the vanilla loot table
     */
    @Nullable
    public NamespacedKey getVanillaLootTableKey() {
        return this.vanillaLootTableKey;
    }

    /**
     * @return the NamespacedKey of the advancement
     */
    @Nullable
    public NamespacedKey getAdvancementKey() {
        return this.advancementKey;
    }

    /**
     * @return the type of explosion or null if there was none
     */
    @Nullable
    public ExplosionType getExplosionType() {
        if (this.explosionType != null)
            return this.explosionType;

        if (this.looter instanceof Creeper && ((Creeper) this.looter).isPowered())
            return ExplosionType.CHARGED_ENTITY;

        return null;
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
    public double getLuckLevel() {
        double luck = 0;
        if (this.looter != null && this.looter instanceof LivingEntity) {
            AttributeInstance attribute = ((LivingEntity) this.looter).getAttribute(Attribute.GENERIC_LUCK);
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
     * @return the ItemStack used by the looter
     */
    @Nullable
    public ItemStack getItemUsed() {
        if (this.looter == null || !(this.looter instanceof LivingEntity))
            return null;

        EntityEquipment equipment = ((LivingEntity) this.looter).getEquipment();
        if (equipment == null)
            return null;

        if (equipment.getItemInMainHand().getType() != Material.AIR) {
            return equipment.getItemInMainHand();
        } else if (equipment.getItemInOffHand().getType() != Material.AIR) {
            return equipment.getItemInOffHand();
        } else {
            return null;
        }
    }

    /**
     * @return true if the already generated loot (from server events) created any items, false otherwise
     */
    public boolean hasExistingItems() {
        return this.hasExistingItems;
    }

    /**
     * @return the LootPlaceholders used to parse placeholders within loot item strings
     */
    @NotNull
    public LootPlaceholders getPlaceholders() {
        return this.placeholders;
    }

    /**
     * Adds placeholders relative to this context
     */
    private void addContextPlaceholders() {
        if (this.getLootingPlayer() != null) this.placeholders.add("player", this.getLootingPlayer().getName());
        if (this.getLootedEntity() != null) this.placeholders.add("entity_type", this.getLootedEntity().getType().name().toLowerCase());
        if (this.getLootedBlock() != null) this.placeholders.add("block_type", this.getLootedBlock().getType().name().toLowerCase());
        if (this.getItemUsed() != null) this.placeholders.add("item_type", this.getItemUsed().getType().name().toLowerCase());
        if (this.getVanillaLootTableKey() != null) this.placeholders.add("vanilla_loot_table_name", this.getVanillaLootTableKey().toString());
        if (this.getAdvancementKey() != null) this.placeholders.add("advancement_name", this.getAdvancementKey().toString());
        if (this.getExplosionType() != null) this.placeholders.add("explosion_type", this.getExplosionType().name().toLowerCase());
        this.placeholders.add("luck_level", this.getLuckLevel());

        Location location = this.getLocation();
        World world = location.getWorld();
        if (world != null)
            this.placeholders.add("world", world.getName());

        this.placeholders.add("x", LootUtils.getToMaximumDecimals(location.getX(), 2));
        this.placeholders.add("y", LootUtils.getToMaximumDecimals(location.getY(), 2));
        this.placeholders.add("z", LootUtils.getToMaximumDecimals(location.getZ(), 2));
    }

    /**
     * @return a new LootContext builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Entity looter;
        private LivingEntity lootedEntity;
        private Block lootedBlock;
        private FishHook fishHook;
        private ItemStack inputItem;
        private NamespacedKey vanillaLootTableKey, advancementKey;
        private ExplosionType explosionType;
        private boolean hasExistingItmes;

        /**
         * Sets the looting entity
         *
         * @param looter The looting entity
         * @return This builder
         */
        public Builder looter(Entity looter) {
            this.looter = looter;
            return this;
        }

        /**
         * Sets the looted entity
         *
         * @param lootedEntity The looted entity
         * @return This builder
         */
        public Builder lootedEntity(LivingEntity lootedEntity) {
            this.lootedEntity = lootedEntity;
            return this;
        }

        /**
         * Sets the looted block
         *
         * @param lootedBlock The looted block
         * @return This builder
         */
        public Builder lootedBlock(Block lootedBlock) {
            this.lootedBlock = lootedBlock;
            return this;
        }

        /**
         * Sets the fishing hook
         *
         * @param fishHook The fishing hook
         * @return This builder
         */
        public Builder fishHook(FishHook fishHook) {
            this.fishHook = fishHook;
            return this;
        }

        /**
         * Sets the input item
         *
         * @param inputItem The input item
         * @return This builder
         */
        public Builder inputItem(ItemStack inputItem) {
            this.inputItem = inputItem;
            return this;
        }

        /**
         * Sets the vanilla loot table key
         *
         * @param vanillaLootTableKey The vanilla loot table key
         * @return This builder
         */
        public Builder vanillaLootTableKey(NamespacedKey vanillaLootTableKey) {
            this.vanillaLootTableKey = vanillaLootTableKey;
            return this;
        }

        /**
         * Sets the advancement key
         *
         * @param advancementKey The advancement key
         * @return This builder
         */
        public Builder advancementKey(NamespacedKey advancementKey) {
            this.advancementKey = advancementKey;
            return this;
        }

        /**
         * Sets the explosion type
         *
         * @param explosionType The explosion type
         * @return This builder
         */
        public Builder explosionType(ExplosionType explosionType) {
            this.explosionType = explosionType;
            return this;
        }

        /**
         * Sets whether the loot table has existing items
         *
         * @param hasExistingItmes Whether the loot table has existing items
         * @return This builder
         */
        public Builder hasExistingItems(boolean hasExistingItmes) {
            this.hasExistingItmes = hasExistingItmes;
            return this;
        }

        /**
         * Builds the LootContext
         *
         * @return The built LootContext
         * @throws IllegalStateException If the builder has not been configured with something that can provide a Location
         */
        public LootContext build() {
            LootContext lootContext = new LootContext(
                    this.looter,
                    this.lootedEntity,
                    this.lootedBlock,
                    this.fishHook,
                    this.inputItem,
                    this.vanillaLootTableKey,
                    this.advancementKey,
                    this.explosionType,
                    this.hasExistingItmes
            );
            lootContext.getLocation();
            return lootContext;
        }

    }

}
