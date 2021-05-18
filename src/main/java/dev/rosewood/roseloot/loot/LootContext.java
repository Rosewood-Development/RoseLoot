package dev.rosewood.roseloot.loot;

import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class LootContext {

    private final LivingEntity looter;
    private final LivingEntity lootedEntity;
    private final Block lootedBlock;

    public LootContext(@Nullable LivingEntity looter, @NotNull LivingEntity lootedEntity) {
        this.looter = looter;
        this.lootedEntity = lootedEntity;
        this.lootedBlock = null;
    }

    public LootContext(@Nullable LivingEntity looter, @NotNull Block lootedBlock) {
        this.looter = looter;
        this.lootedBlock = lootedBlock;
        this.lootedEntity = null;
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
     * @return the Location for this context
     */
    public Location getLocation() {
        if (this.lootedEntity != null) return this.lootedEntity.getLocation();
        if (this.lootedBlock != null) return this.lootedBlock.getLocation();
        if (this.looter != null) return this.looter.getLocation();
        throw new IllegalStateException("LootContext does not have a Location");
    }

    /**
     * @return the luck level for this context, used for bonus rolls
     */
    public int getLuckLevel() {
        if (this.looter == null)
            return 0;
        AttributeInstance attribute = this.looter.getAttribute(Attribute.GENERIC_LUCK);
        return attribute != null ? (int) attribute.getValue() : 0;
    }

}
