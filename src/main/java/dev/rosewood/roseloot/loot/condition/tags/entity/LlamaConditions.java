package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Llama;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.LlamaInventory;

public class LlamaConditions extends EntityConditions {

    public LlamaConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("llama-decor", LlamaDecorCondition.class);
        event.registerLootCondition("llama-color", LlamaColorCondition.class);
    }

    public static class LlamaDecorCondition extends LootCondition {

        private List<Material> decorMaterials;

        public LlamaDecorCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            return context.getAs(LootContextParams.LOOTED_ENTITY, Llama.class)
                    .map(Llama::getInventory)
                    .map(LlamaInventory::getDecor)
                    .map(ItemStack::getType)
                    .filter(this.decorMaterials::contains)
                    .isPresent();
        }

        @Override
        public boolean parseValues(String[] values) {
            this.decorMaterials = new ArrayList<>();

            for (String value : values) {
                try {
                    Material decorMaterial = Material.matchMaterial(value);
                    if (decorMaterial != null)
                        this.decorMaterials.add(decorMaterial);
                } catch (Exception ignored) { }
            }

            return !this.decorMaterials.isEmpty();
        }

    }

    public static class LlamaColorCondition extends LootCondition {

        private List<Llama.Color> colors;

        public LlamaColorCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            return context.getAs(LootContextParams.LOOTED_ENTITY, Llama.class)
                    .map(Llama::getColor)
                    .filter(this.colors::contains)
                    .isPresent();
        }

        @Override
        public boolean parseValues(String[] values) {
            this.colors = new ArrayList<>();

            for (String value : values) {
                try {
                    Llama.Color color = Llama.Color.valueOf(value.toUpperCase());
                    this.colors.add(color);
                } catch (Exception ignored) { }
            }

            return !this.colors.isEmpty();
        }

    }

}
