package dev.rosewood.roseloot.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParam;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.ConfigurationManager;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.rosestacker.event.EntityStackMultipleDeathEvent;
import dev.rosewood.rosestacker.stack.StackedEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class RoseStackerEntityDeathListener implements Listener {

    public static final LootContextParam<StackedEntity> STACKED_ENTITY = LootContextParams.create("rosestacker_stacked_entity", StackedEntity.class, builder ->
            builder.withPlaceholders((x, y) -> y.add("rosestacker_entity_stack_size", x.getStackSize())));

    private final RosePlugin rosePlugin;
    private final LootTableManager lootTableManager;

    public RoseStackerEntityDeathListener(RosePlugin rosePlugin) {
        this.rosePlugin = rosePlugin;
        this.lootTableManager = rosePlugin.getManager(LootTableManager.class);
    }

    @EventHandler
    public void onEntityStackMultipleDeath(EntityStackMultipleDeathEvent event) {
        LivingEntity mainEntity = event.getStack().getEntity();
        if (ConfigurationManager.Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(mainEntity.getWorld().getName())))
            return;

        Entity looter = null;
        if (mainEntity.getLastDamageCause() instanceof EntityDamageByEntityEvent)
            looter = ((EntityDamageByEntityEvent) mainEntity.getLastDamageCause()).getDamager();

        Map<Enchantment, Integer> enchantmentLevels = null;
        if (looter != null) {
            ItemStack itemUsed = LootUtils.getEntityItemUsed(looter);
            if (itemUsed != null)
                enchantmentLevels = itemUsed.getEnchantments();
        }

        List<LootContents> extras = new ArrayList<>();
        for (Map.Entry<LivingEntity, EntityStackMultipleDeathEvent.EntityDrops> entry : event.getEntityDrops().entrySet()) {
            LivingEntity entity = entry.getKey();
            EntityStackMultipleDeathEvent.EntityDrops drops = entry.getValue();

            LootContext lootContext = LootContext.builder(LootUtils.getEntityLuck(looter), enchantmentLevels)
                    .put(LootContextParams.ORIGIN, entity.getLocation())
                    .put(LootContextParams.LOOTER, looter)
                    .put(LootContextParams.LOOTED_ENTITY, entity)
                    .put(STACKED_ENTITY, event.getStack())
                    .put(LootContextParams.EXPLOSION_TYPE, LootUtils.getDeathExplosionType(entity))
                    .put(LootContextParams.HAS_EXISTING_ITEMS, !drops.getDrops().isEmpty())
                    .build();
            LootResult lootResult = this.lootTableManager.getLoot(LootTableTypes.ENTITY, lootContext);
            LootContents lootContents = lootResult.getLootContents();

            // Overwrite existing drops if applicable
            if (lootResult.shouldOverwriteItems())
                drops.getDrops().clear();

            if (lootResult.shouldOverwriteExperience())
                drops.setExperience(0);

            // Add items to drops and adjust experience
            drops.getDrops().addAll(lootContents.getItems());
            drops.setExperience(drops.getExperience() + lootContents.getExperience());

            extras.add(lootContents);
        }

        Runnable task = () -> extras.forEach(x -> x.triggerExtras(mainEntity.getLocation()));
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(this.rosePlugin, task);
        } else {
            task.run();
        }
    }

}
