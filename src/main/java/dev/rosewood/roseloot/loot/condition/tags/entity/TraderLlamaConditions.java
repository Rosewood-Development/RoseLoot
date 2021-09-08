package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.event.LootConditionRegistrationEvent;
import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.TraderLlama;
import org.bukkit.inventory.ItemStack;

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
            LivingEntity looted = context.getLootedEntity();
            if (!(looted instanceof TraderLlama))
                return false;
            ItemStack decor = ((TraderLlama) looted).getInventory().getDecor();
            if (decor == null)
                return false;
            return this.decorMaterials.contains(decor.getType());
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

        private List<Llama.Color> types;

        public TraderLlamaColorCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            LivingEntity looted = context.getLootedEntity();
            if (!(looted instanceof TraderLlama))
                return false;
            return this.types.contains(((TraderLlama) looted).getColor());
        }

        @Override
        public boolean parseValues(String[] values) {
            this.types = new ArrayList<>();

            for (String value : values) {
                try {
                    Llama.Color color = Llama.Color.valueOf(value.toUpperCase());
                    this.types.add(color);
                } catch (Exception ignored) { }
            }

            return !this.types.isEmpty();
        }

    }

}
