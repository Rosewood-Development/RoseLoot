package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

public class ToolTagCondition extends LootCondition {

    private List<Tag<Material>> tags;

    public ToolTagCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        ItemStack itemStack = context.getItemUsed();
        if (itemStack == null)
            return false;

        Material itemType = itemStack.getType();
        return this.tags.stream().anyMatch(x -> x.isTagged(itemType));
    }

    @Override
    public boolean parseValues(String[] values) {
        this.tags = new ArrayList<>();

        for (String value : values) {
            for (Tag<Material> tag : Bukkit.getTags("items", Material.class)) {
                if (tag.getKey().getKey().equalsIgnoreCase(value)) {
                    this.tags.add(tag);
                    break;
                }
            }
        }

        return !this.tags.isEmpty();
    }

}
