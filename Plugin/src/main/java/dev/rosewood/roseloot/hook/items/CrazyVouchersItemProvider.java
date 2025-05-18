package dev.rosewood.roseloot.hook.items;

import com.badbones69.crazyvouchers.CrazyVouchers;
import com.badbones69.crazyvouchers.api.objects.Voucher;
import dev.rosewood.roseloot.loot.context.LootContext;
import org.bukkit.inventory.ItemStack;

public class CrazyVouchersItemProvider extends ItemProvider {

    public CrazyVouchersItemProvider() {
        super("CrazyVouchers", true);
    }

    @Override
    public ItemStack getItem(LootContext context, String id) {
        if (!this.isEnabled())
            return null;

        Voucher voucher = CrazyVouchers.get().getCrazyManager().getVoucher(id);
        if (voucher == null)
            return null;

        return voucher.buildItem();
    }

    @Override
    public String getItemId(ItemStack item) {
        if (!this.isEnabled())
            return null;

        Voucher voucher = CrazyVouchers.get().getCrazyManager().getVoucherFromItem(item);
        if (voucher == null)
            return null;

        return voucher.getName();
    }

}
