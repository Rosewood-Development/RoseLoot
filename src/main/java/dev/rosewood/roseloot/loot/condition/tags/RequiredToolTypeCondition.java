package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RequiredToolTypeCondition extends LootCondition {

    private List<Material> toolTypes;

    public RequiredToolTypeCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        ItemStack itemStack = context.getItemUsed();
        if (itemStack == null)
            return false;

        return this.toolTypes.contains(itemStack.getType());
    }

    @Override
    public boolean parseValues(String[] values) {
        this.toolTypes = new ArrayList<>();

        for (String value : values) {
            Material toolType = Material.matchMaterial(value);
            if (toolType != null)
                this.toolTypes.add(toolType);
        }

        return !this.toolTypes.isEmpty();
    }

}
