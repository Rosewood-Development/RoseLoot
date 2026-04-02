package dev.rosewood.roseloot.listener.hook;

import org.bukkit.block.BlockState;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.EntitySpawnUtil;
import dev.rosewood.roseloot.config.SettingKey;
import dev.rosewood.roseloot.listener.helper.LazyLootTableListener;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.OverwriteExisting;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
import me.lauriichan.spigot.justlootit.api.event.player.JLIPlayerLootProvidedEvent;
import me.lauriichan.spigot.justlootit.api.event.player.JLIPlayerVanillaLootProvidedEvent;

public class JustLootItLootGenerateListener extends LazyLootTableListener {

    public JustLootItLootGenerateListener(RosePlugin rosePlugin) {
        super(rosePlugin, LootTableTypes.CONTAINER);
    }

    @EventHandler
    public void onLootProvided(JLIPlayerLootProvidedEvent event) {
        if (this.rosePlugin.getRoseConfig().get(SettingKey.DISABLED_WORLDS).stream().anyMatch(x -> x.equalsIgnoreCase(event.entryLocation().getWorld().getName())))
            return;

        Player looter = event.player().asBukkit();

        LootContext.Builder contextBuilder = LootContext.builder(LootUtils.getEntityLuck(looter))
            .put(LootContextParams.ORIGIN, event.entryLocation())
            .put(LootContextParams.LOOTER, looter)
            // Usually JLI loot containers always contain loot when this event is triggered
            // There is only the exception for when a loot table doesn't generate any or a static container is kept empty for unknown reasons
            // However that should be the exception so we just *expect* loot to be there
            .put(LootContextParams.HAS_EXISTING_ITEMS, true);
        if (event.entryHolder() instanceof BlockState state)
            contextBuilder.put(LootContextParams.LOOTED_BLOCK, state.getBlock());
        if (event instanceof JLIPlayerVanillaLootProvidedEvent vanillaEvent)
            contextBuilder.put(LootContextParams.VANILLA_LOOT_TABLE_KEY, vanillaEvent.lootTable().getKey());

        LootResult lootResult = this.rosePlugin.getManager(LootTableManager.class).getLoot(LootTableTypes.CONTAINER, contextBuilder.build());
        if (lootResult.isEmpty())
            return;

        LootContents lootContents = lootResult.getLootContents();

        // Overwrite existing loot if applicable
        if (lootResult.doesOverwriteExisting(OverwriteExisting.ITEMS))
            event.inventory().clear();

        // Set items and drop experience
        event.bukkitInventory().addItem(lootResult.getLootContents().getItems().toArray(ItemStack[]::new));

        int experience = lootContents.getExperience();
        if (experience > 0)
            EntitySpawnUtil.spawn(looter.getLocation(), ExperienceOrb.class, x -> x.setExperience(experience));

        lootContents.triggerExtras(event.entryLocation());
    }

}
