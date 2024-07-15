package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.VoucherUtils;
import java.util.Optional;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class VoucherLootItem extends ItemLootItem {

    private final String lootTable;

    protected VoucherLootItem(String lootTable, ItemLootItem base) {
        super(base);
        this.lootTable = lootTable;
        this.resolveItem(LootContext.none());
    }

    @Override
    protected Optional<ItemStack> resolveItem(LootContext context) {
        return super.resolveItem(context).map(item -> {
            VoucherUtils.setVoucherData(item, this.lootTable);
            return item;
        });
    }

    public static VoucherLootItem fromSection(ConfigurationSection section) {
        String lootTable = section.getString("loottable");
        if (lootTable == null)
            return null;

        ItemLootItem base = ItemLootItem.fromSection(section);
        if (base == null)
            return null;

        return new VoucherLootItem(lootTable, base);
    }

}
