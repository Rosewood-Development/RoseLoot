package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.LivingEntity;

public class EndermanConditions extends EntityConditions {

    public EndermanConditions() {
        LootConditions.registerTag("enderman-holding-block", EndermanHoldingBlockCondition.class);
    }

    public static class EndermanHoldingBlockCondition extends LootCondition {

        private List<Material> blockTypes;

        public EndermanHoldingBlockCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            LivingEntity entity = context.getLootedEntity();
            if (!(entity instanceof Enderman))
                return false;

            Enderman enderman = (Enderman) entity;
            if (enderman.getCarriedBlock() == null)
                return false;

            return this.blockTypes.isEmpty() || this.blockTypes.contains(enderman.getCarriedBlock().getMaterial());
        }

        @Override
        public boolean parseValues(String[] values) {
            this.blockTypes = new ArrayList<>();

            for (String value : values) {
                try {
                    Material blockMaterial = Material.matchMaterial(value);
                    if (blockMaterial != null && blockMaterial.isBlock())
                        this.blockTypes.add(blockMaterial);
                } catch (Exception ignored) { }
            }

            return true;
        }

    }

}
