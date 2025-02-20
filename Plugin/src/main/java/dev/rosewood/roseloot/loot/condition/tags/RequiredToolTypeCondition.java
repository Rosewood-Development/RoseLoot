package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RequiredToolTypeCondition extends BaseLootCondition {

    private List<Material> toolTypes;

    public RequiredToolTypeCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        return context.getItemUsed()
                .map(ItemStack::getType)
                .filter(this.toolTypes::contains)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        this.toolTypes = new ArrayList<>();

        for (String value : values) {
            if (value.startsWith("#")) {
                Set<Material> tagBlocks = LootUtils.getTagValues(value.substring(1), Material.class, "items");
                if (tagBlocks != null) {
                    this.toolTypes.addAll(tagBlocks);
                    continue;
                }
            }

            Material toolType = Material.matchMaterial(value);
            if (toolType != null)
                this.toolTypes.add(toolType);
        }

        return !this.toolTypes.isEmpty();
    }

}
