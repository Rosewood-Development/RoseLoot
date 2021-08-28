package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.item.ExplosionLootItem.ExplosionInstance;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ExplosionLootItem implements TriggerableLootItem<ExplosionInstance> {

    private ExplosionInstance explosionInstance;

    public ExplosionLootItem(int power, boolean fire, boolean breakBlocks) {
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
        this.explosionInstance = ExplosionInstance.combine(this.explosionInstance, other.explosionInstance);
        return true;
    }

    @Override
    public void trigger(LootContext context, Player player, Location location) {
        this.create(context).trigger(location);
    }

    public static LootItem<?> fromSection(ConfigurationSection section) {
        return new ExplosionLootItem(section.getInt("power", 3), section.getBoolean("fire", false), section.getBoolean("break-blocks", true));
    }

    public static class ExplosionInstance {

        private final int power;
        private final boolean fire;
        private final boolean breakBlocks;

        public ExplosionInstance(int power, boolean fire, boolean breakBlocks) {
            this.power = power;
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
            if (world != null)
                world.createExplosion(location, this.power, this.fire, this.breakBlocks);
        }

        /**
         * Combines two ExplosionStates into one, using the higher values between the two
         *
         * @param first The first ExplosionState
         * @param second The second ExplosionState
         * @return an ExplosionState with the combined values of the two
         */
        public static ExplosionInstance combine(ExplosionInstance first, ExplosionInstance second) {
            int power = Math.max(first.power, second.power);
            boolean fire = first.fire | second.fire;
            boolean breakBlocks = first.breakBlocks | second.breakBlocks;
            return new ExplosionInstance(power, fire, breakBlocks);
        }

    }

}
