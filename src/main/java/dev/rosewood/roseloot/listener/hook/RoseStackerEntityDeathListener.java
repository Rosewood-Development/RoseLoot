package dev.rosewood.roseloot.listener.hook;

import com.google.common.collect.Multimap;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseloot.listener.helper.LazyLootTableListener;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.LootResult;
import dev.rosewood.roseloot.loot.OverwriteExisting;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParam;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.loot.table.LootTableTypes;
import dev.rosewood.roseloot.manager.ConfigurationManager;
import dev.rosewood.roseloot.util.LootUtils;
import dev.rosewood.rosestacker.event.EntityStackMultipleDeathEvent;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class RoseStackerEntityDeathListener extends LazyLootTableListener {

    public static final LootContextParam<Boolean> STACKED_ENTITY = LootContextParams.create("rosestacker_stacked_entity", Boolean.class);
    public static final LootContextParam<Boolean> STACKED_ENTITY_PRIMARY = LootContextParams.create("rosestacker_stacked_entity_primary", Boolean.class);

    public RoseStackerEntityDeathListener(RosePlugin rosePlugin) {
        super(rosePlugin, LootTableTypes.ENTITY);
    }

    @EventHandler
    public void onEntityStackMultipleDeath(EntityStackMultipleDeathEvent event) {
        LivingEntity mainEntity = event.getStack().getEntity();
        if (ConfigurationManager.Setting.DISABLED_WORLDS.getStringList().stream().anyMatch(x -> x.equalsIgnoreCase(mainEntity.getWorld().getName())))
            return;

        Entity looter = null;
        if (mainEntity.getLastDamageCause() instanceof EntityDamageByEntityEvent lastDamageCause)
            looter = lastDamageCause.getDamager();

        Map<Enchantment, Integer> enchantmentLevels = null;
        if (looter != null) {
            ItemStack itemUsed = LootUtils.getEntityItemUsed(looter);
            if (itemUsed != null)
                enchantmentLevels = itemUsed.getEnchantments();
        }

        LootContext groupedExtrasContext = LootContext.builder()
                .put(LootContextParams.ORIGIN, mainEntity.getLocation())
                .put(LootContextParams.LOOTER, looter)
                .build();
        LootContents groupedExtras = new LootContents(groupedExtrasContext);

        boolean primaried = false;
        Multimap<LivingEntity, EntityStackMultipleDeathEvent.EntityDrops> stackDrops = event.getEntityDrops();
        for (LivingEntity entity : stackDrops.keySet()) {
            for (EntityStackMultipleDeathEvent.EntityDrops drops : event.getEntityDrops().get(entity)) {
                LootContext lootContext = LootContext.builder(LootUtils.getEntityLuck(looter), enchantmentLevels)
                        .put(LootContextParams.ORIGIN, entity.getLocation())
                        .put(LootContextParams.LOOTER, looter)
                        .put(LootContextParams.LOOTED_ENTITY, entity)
                        .put(STACKED_ENTITY, true)
                        .put(STACKED_ENTITY_PRIMARY, !primaried)
                        .put(LootContextParams.EXPLOSION_TYPE, LootUtils.getDeathExplosionType(entity))
                        .put(LootContextParams.HAS_EXISTING_ITEMS, !drops.getDrops().isEmpty())
                        .build();
                lootContext.addPlaceholder("rosestacker_entity_stack_size", event.getEntityDrops().size());
                primaried = true;

                LootResult lootResult = LOOT_TABLE_MANAGER.getLoot(LootTableTypes.ENTITY, lootContext);
                if (lootResult.isEmpty())
                    continue;

                LootContents lootContents = lootResult.getLootContents();

                // Overwrite existing drops if applicable
                if (lootResult.doesOverwriteExisting(OverwriteExisting.ITEMS))
                    drops.getDrops().clear();

                if (lootResult.doesOverwriteExisting(OverwriteExisting.EXPERIENCE))
                    drops.setExperience(0);

                // Add items to drops and adjust experience
                drops.getDrops().addAll(lootContents.getItems());
                drops.setExperience(drops.getExperience() + lootContents.getExperience());

                groupedExtras.add(lootContents.getExtras());
                lootContext.copyPlaceholders(groupedExtrasContext);
            }
        }

        Runnable task = () -> groupedExtras.triggerExtras(mainEntity.getLocation());
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(this.rosePlugin, task);
        } else {
            task.run();
        }
    }

}
