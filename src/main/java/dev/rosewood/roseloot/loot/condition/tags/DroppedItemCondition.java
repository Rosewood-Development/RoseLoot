package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DroppedItemCondition extends LootCondition {

    private List<Material> materials;

    public DroppedItemCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return context.get(LootContextParams.INPUT_ITEM)
                .map(ItemStack::getType)
                .filter(this.materials::contains)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        this.materials = new ArrayList<>();

        for (String value : values) {
            try {
                if (value.startsWith("#")) {
                    Set<Material> tagBlocks = LootUtils.getTags(value.substring(1), Material.class, "blocks");
                    if (tagBlocks != null) {
                        this.materials.addAll(tagBlocks);
                        continue;
                    }

                    Set<Material> tagItems = LootUtils.getTags(value.substring(1), Material.class, "items");
                    if (tagItems != null) {
                        this.materials.addAll(tagItems);
                        continue;
                    }
                }

                Material blockMaterial = Material.matchMaterial(value);
                if (blockMaterial != null && blockMaterial.isBlock())
                    this.materials.add(blockMaterial);
            } catch (Exception ignored) { }
        }

        return !this.materials.isEmpty();
    }

}
