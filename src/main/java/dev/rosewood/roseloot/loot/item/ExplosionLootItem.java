package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootContext;
import org.bukkit.Location;
import org.bukkit.World;

public class ExplosionLootItem extends LootItem {

    private final ExplosionState explosionState;

    public ExplosionLootItem(int power, boolean fire, boolean breakBlocks) {
        this.explosionState = new ExplosionState(power, fire, breakBlocks);
    }

    @Override
    public LootContents generate(LootContext context) {
        return LootContents.ofExplosion(this.explosionState);
    }

    public static class ExplosionState {

        private final int power;
        private final boolean fire;
        private final boolean breakBlocks;

        public ExplosionState(int power, boolean fire, boolean breakBlocks) {
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
        public static ExplosionState combine(ExplosionState first, ExplosionState second) {
            int power = Math.max(first.power, second.power);
            boolean fire = first.fire | second.fire;
            boolean breakBlocks = first.breakBlocks | second.breakBlocks;
            return new ExplosionState(power, fire, breakBlocks);
        }

    }

}
