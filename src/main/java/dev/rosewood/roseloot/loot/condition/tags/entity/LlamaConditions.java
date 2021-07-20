package dev.rosewood.roseloot.loot.condition.tags.entity;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.EntityConditions;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.LootConditions;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.inventory.ItemStack;

public class LlamaConditions extends EntityConditions {

    public LlamaConditions() {
        LootConditions.registerTag("llama-decor", LlamaDecorCondition.class);
        LootConditions.registerTag("llama-color", LlamaColorCondition.class);
    }

    public static class LlamaDecorCondition extends LootCondition {

        private List<Material> decorMaterials;

        public LlamaDecorCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            LivingEntity looted = context.getLootedEntity();
            if (!(looted instanceof Llama))
                return false;
            ItemStack decor = ((Llama) looted).getInventory().getDecor();
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

    public static class LlamaColorCondition extends LootCondition {

        private List<Llama.Color> types;

        public LlamaColorCondition(String tag) {
            super(tag);
        }

        @Override
        public boolean checkInternal(LootContext context) {
            LivingEntity looted = context.getLootedEntity();
            if (!(looted instanceof Llama))
                return false;
            return this.types.contains(((Llama) looted).getColor());
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
