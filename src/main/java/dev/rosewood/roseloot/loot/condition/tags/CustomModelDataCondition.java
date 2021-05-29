package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomModelDataCondition extends LootCondition {

    private List<Integer> customModelDataValues;

    public CustomModelDataCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        ItemStack item = context.getItemUsed();
        if (item == null)
            return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return false;

        return this.customModelDataValues.contains(meta.getCustomModelData());
    }

    @Override
    public boolean parseValues(String[] values) {
        this.customModelDataValues = new ArrayList<>();

        for (String value : values) {
            try {
                this.customModelDataValues.add(Integer.parseInt(value));
            } catch (Exception ignored) { }
        }

        return !this.customModelDataValues.isEmpty();
    }

}
