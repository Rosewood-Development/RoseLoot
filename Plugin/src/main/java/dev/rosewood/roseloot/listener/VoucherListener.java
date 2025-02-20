package dev.rosewood.roseloot.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.LootTable;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.manager.LocaleManager;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.roseloot.util.VoucherUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class VoucherListener implements Listener {

    private final RosePlugin rosePlugin;

    public VoucherListener(RosePlugin rosePlugin) {
        this.rosePlugin = rosePlugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onVoucherRedeem(PlayerInteractEvent event) {
        // Want to be able to execute this regardless if it's cancelled or not
        if ((event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
                || event.getHand() != EquipmentSlot.HAND
                || event.useInteractedBlock() == Event.Result.DENY)
            return;

        Block block = event.getClickedBlock();
        if (block != null && block.getType().isInteractable())
            return;

        ItemStack itemInHand = event.getItem();
        if (itemInHand == null || itemInHand.getType() == Material.AIR)
            return;

        String lootTableName = VoucherUtils.getVoucherLootTableName(itemInHand);
        if (lootTableName == null)
            return;

        Player player = event.getPlayer();
        LootTable lootTable = this.rosePlugin.getManager(LootTableManager.class).getLootTable(lootTableName);
        if (lootTable == null) {
            this.rosePlugin.getManager(LocaleManager.class).sendMessage(player, "voucher-expired");
            return;
        }

        // Remove one of the vouchers
        itemInHand.setAmount(itemInHand.getAmount() - 1);

        LootContext lootContext = LootContext.builder(LootUtils.getEntityLuck(player))
                .put(LootContextParams.ORIGIN, player.getLocation())
                .put(LootContextParams.LOOTER, player)
                .build();
        LootResult lootResult = this.rosePlugin.getManager(LootTableManager.class).getLoot(lootTable, lootContext);
        lootResult.getLootContents().dropForPlayer(player);

        event.setUseItemInHand(Event.Result.DENY);
        event.setUseInteractedBlock(Event.Result.DENY);
    }

}
