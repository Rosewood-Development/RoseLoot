package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Enderman;

public class EndermanConditions extends EntityConditions {

    public EndermanConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("enderman-holding-block", EndermanHoldingBlockCondition.class);
    }

    public static class EndermanHoldingBlockCondition extends LootCondition {

        private List<Material> blockTypes;

        public EndermanHoldingBlockCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            return context.getAs(LootContextParams.LOOTED_ENTITY, Enderman.class)
                    .map(Enderman::getCarriedBlock)
                    .map(BlockData::getMaterial)
                    .filter(this.blockTypes::contains)
                    .isPresent();
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
