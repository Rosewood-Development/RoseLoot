package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.item.ExplosionLootItem.ExplosionInstance;
import dev.rosewood.roseloot.util.NumberProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class ExplosionLootItem implements TriggerableLootItem<ExplosionInstance> {

    private final ExplosionInstance explosionInstance;

    public ExplosionLootItem(NumberProvider power, boolean fire, boolean breakBlocks) {
        this.explosionInstance = new ExplosionInstance(power, fire, breakBlocks);
    }

    @Override
    public ExplosionInstance create(LootContext context) {
        return this.explosionInstance;
    }

    @Override
    public boolean combineWith(LootItem<?> lootItem) {
        if (!(lootItem instanceof ExplosionLootItem))
            return false;

        ExplosionLootItem other = (ExplosionLootItem) lootItem;
        this.explosionInstance.combineWith(other.explosionInstance);
        return true;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        this.create(context).trigger(location);
    }

    public static ExplosionLootItem fromSection(ConfigurationSection section) {
        NumberProvider power = NumberProvider.fromSection(section, "power", 3);
        boolean fire = section.getBoolean("fire", false);
        boolean breakBlocks = section.getBoolean("break-blocks", true);
        return new ExplosionLootItem(power, fire, breakBlocks);
    }

    public static class ExplosionInstance {

        private final List<NumberProvider> powers;
        private boolean fire;
        private boolean breakBlocks;

        public ExplosionInstance(NumberProvider power, boolean fire, boolean breakBlocks) {
            this.powers = new ArrayList<>(Collections.singletonList(power));
            this.fire = fire;
            this.breakBlocks = breakBlocks;
        }

        /**
         * Triggers the stored explosion state
         *
         * @param location The Location to trigger the explosion at
         */
        public void trigger(Location location) {
            World world = location.getWorld();
            if (world != null) {
                int power = this.powers.stream().mapToInt(NumberProvider::getInteger).max().orElse(0);
                world.createExplosion(location, power, this.fire, this.breakBlocks);
            }
        }

        /**
         * Merges another ExlosionInstance with this one using the higher values between the two
         *
         * @param other The other ExplosionState
         */
        public void combineWith(ExplosionInstance other) {
            this.powers.addAll(other.powers);
            this.fire |= other.fire;
            this.breakBlocks |= other.breakBlocks;
        }

    }

}
