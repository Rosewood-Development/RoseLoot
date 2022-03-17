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
import org.bukkit.entity.TraderLlama;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.LlamaInventory;

public class TraderLlamaConditions extends EntityConditions {

    public TraderLlamaConditions(LootConditionRegistrationEvent event) {
        event.registerLootCondition("trader-llama-decor", TraderLlamaDecorCondition.class);
        event.registerLootCondition("trader-llama-color", TraderLlamaColorCondition.class);
    }

    public static class TraderLlamaDecorCondition extends LootCondition {

        private List<Material> decorMaterials;

        public TraderLlamaDecorCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            return context.getAs(LootContextParams.LOOTED_ENTITY, TraderLlama.class)
                    .map(TraderLlama::getInventory)
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
                    if (decorMaterial != null && decorMaterial.isBlock())
                        this.decorMaterials.add(decorMaterial);
                } catch (Exception ignored) { }
            }

            return !this.decorMaterials.isEmpty();
        }

    }

    public static class TraderLlamaColorCondition extends LootCondition {

        private List<Llama.Color> colors;

        public TraderLlamaColorCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            return context.getAs(LootContextParams.LOOTED_ENTITY, TraderLlama.class)
                    .map(TraderLlama::getColor)
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
