package dev.rosewood.roseloot.loot.item;

import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.loot.RelativeTo;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.util.EnumHelper;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class SpawnEntityLootItem implements TriggerableLootItem {

    private static final boolean HAS_ENTITY_SNAPSHOT = NMSUtil.getVersionNumber() > 20 || (NMSUtil.getVersionNumber() == 20 && NMSUtil.getMinorVersionNumber() >= 6);

    private final EntitySpawner spawner;
    private final boolean copyLootedEntity;
    private final RelativeTo relativeTo;
    private final NumberProvider xOffset;
    private final NumberProvider yOffset;
    private final NumberProvider zOffset;

    protected SpawnEntityLootItem(EntitySpawner spawner, boolean copyLootedEntity, RelativeTo relativeTo, NumberProvider xOffset, NumberProvider yOffset, NumberProvider zOffset) {
        this.spawner = spawner;
        this.copyLootedEntity = copyLootedEntity;
        this.relativeTo = relativeTo;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        Location entityLocation = switch (this.relativeTo) {
            case LOOTER -> context.get(LootContextParams.LOOTER).map(Entity::getLocation).orElse(null);
            default -> location;
        };

        if (entityLocation == null)
            return;

        entityLocation = entityLocation.clone().add(this.xOffset.getDouble(context), this.yOffset.getDouble(context), this.zOffset.getDouble(context));

        if (this.copyLootedEntity && HAS_ENTITY_SNAPSHOT) {
            Optional<LivingEntity> lootedEntityOptional = context.get(LootContextParams.LOOTED_ENTITY);
            if (lootedEntityOptional.isEmpty())
                return;

            LivingEntity lootedEntity = lootedEntityOptional.get();
            if (lootedEntity.getType() == EntityType.PLAYER)
                return;

            Entity copy = lootedEntity.copy(entityLocation);
            if (copy instanceof LivingEntity livingCopy)
                livingCopy.setHealth(livingCopy.getAttribute(Attribute.MAX_HEALTH).getValue());
        } else {
            this.spawner.spawn(entityLocation);
        }
    }

    public static SpawnEntityLootItem fromSection(ConfigurationSection section) {
        boolean copyLootedEntity = section.getBoolean("copy-looted-entity");

        EntitySpawner spawner = null;
        if (!copyLootedEntity) {
            String entityString = section.getString("entity");
            if (entityString == null)
                return null;

            String entityDataString = null;
            int dataIndex = entityString.indexOf("{");
            if (dataIndex != -1) {
                entityDataString = entityString.substring(dataIndex);
                entityString = entityString.substring(0, dataIndex);
            }

            if (HAS_ENTITY_SNAPSHOT && entityDataString != null) {
                spawner = EntitySnapshotSpawner.parse(entityDataString);
            } else {
                EntityType entityType = EnumHelper.valueOf(EntityType.class, entityString);
                if (entityType == null)
                    return null;
                spawner = new EntityTypeSpawner(entityType);
            }

            if (spawner == null)
                return null;
        }

        RelativeTo relativeTo = EnumHelper.valueOf(RelativeTo.class, section.getString("relative-to"), RelativeTo.LOOTED);
        NumberProvider x = NumberProvider.fromSection(section, "x", 0);
        NumberProvider y = NumberProvider.fromSection(section, "y", 0);
        NumberProvider z = NumberProvider.fromSection(section, "z", 0);

        return new SpawnEntityLootItem(spawner, copyLootedEntity, relativeTo, x, y, z);
    }

    protected interface EntitySpawner {
        void spawn(Location location);
    }

    private record EntityTypeSpawner(EntityType type) implements EntitySpawner {
        @Override
        public void spawn(Location location) {
            location.getWorld().spawnEntity(location, this.type);
        }
    }

    private record EntitySnapshotSpawner(EntitySnapshot snapshot) implements EntitySpawner {
        @Override
        public void spawn(Location location) {
            this.snapshot.createEntity(location);
        }

        public static EntitySpawner parse(String input) {
            try {
                EntitySnapshot snapshot = Bukkit.getEntityFactory().createEntitySnapshot(input);
                return new EntitySnapshotSpawner(snapshot);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

}
