package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DroppedItemCondition extends LootCondition {

    private List<Material> materials;

    public DroppedItemCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        ItemStack itemStack = context.getInputItem();
        if (itemStack == null)
            return false;

        return this.materials.contains(itemStack.getType());
    }

    @Override
    public boolean parseValues(String[] values) {
        this.materials = new ArrayList<>();

        for (String value : values) {
            try {
                Material material = Material.matchMaterial(value);
                if (material != null && material.isItem())
                    this.materials.add(material);
            } catch (Exception ignored) { }
        }

        return !this.materials.isEmpty();
    }

}
