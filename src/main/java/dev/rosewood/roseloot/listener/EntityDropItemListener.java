package dev.rosewood.roseloot.listener;

import dev.rosewood.rosegarden.RosePlugin;
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
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockShearEntityEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDropItemListener extends LazyLootTableListener {

    private Reference<Player> lastShearer;

    public EntityDropItemListener(RosePlugin rosePlugin) {
        super(rosePlugin, LootTableTypes.ENTITY_DROP_ITEM);
        this.lastShearer = new WeakReference<>(null);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntitySpawn(CreatureSpawnEvent event) {
        // Tag all spawned entities with the spawn reason
        LootUtils.setEntitySpawnReason(event.getEntity(), event.getSpawnReason());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntitySpawnFromSpawner(SpawnerSpawnEvent event) {
        // Tag all spawned entities with the spawn reason, separate event listener for spawners for custom spawner plugins
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity)
            LootUtils.setEntitySpawnReason((LivingEntity) entity, CreatureSpawnEvent.SpawnReason.SPAWNER);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerShearEntity(PlayerShearEntityEvent event) {
        this.lastShearer = new WeakReference<>(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockShearEntity(BlockShearEntityEvent event) {
        this.lastShearer = new WeakReference<>(null);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDropItem(EntityDropItemEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity))
            return;

        Item item = event.getItemDrop();
        ItemStack itemStack = item.getItemStack();
        if (itemStack.getType() == Material.LEAD)
            return;

        if (this.rosePlugin.getRoseConfig().get(SettingKey.DISABLED_WORLDS).stream().anyMatch(x -> x.equalsIgnoreCase(entity.getWorld().getName())))
            return;

        Player shearer = switch (entity.getType().getKey().getKey()) {
            case "sheep",
                 "snow_golem",
                 "snowman",
                 "mooshroom",
                 "mushroom_cow",
                 "bogged" -> this.lastShearer.get();
            default -> null;
        };

        LootContext lootContext = LootContext.builder(LootUtils.getEntityLuck(shearer))
                .put(LootContextParams.ORIGIN, entity.getLocation())
                .put(LootContextParams.LOOTER, shearer)
                .put(LootContextParams.LOOTED_ENTITY, entity)
                .put(LootContextParams.INPUT_ITEM, itemStack)
                .put(LootContextParams.HAS_EXISTING_ITEMS, true)
                .build();
        LootResult lootResult = this.rosePlugin.getManager(LootTableManager.class).getLoot(LootTableTypes.ENTITY_DROP_ITEM, lootContext);
        if (lootResult.isEmpty())
            return;

        LootContents lootContents = lootResult.getLootContents();

        Location dropLocation = event.getItemDrop().getLocation();

        // Overwrite existing drops if applicable
        if (lootResult.doesOverwriteExisting(OverwriteExisting.ITEMS))
            event.setCancelled(true);

        lootContents.dropAtLocation(dropLocation);
    }

}
