package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.Arrays;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Vibration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ParticleLootItem implements TriggerableLootItem {

    private final ParticleSpawnData particleSpawnData;

    public ParticleLootItem(ParticleSpawnData particleSpawnData) {
        this.particleSpawnData = particleSpawnData;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        // Use the center of the entity or the center of the block, whichever is available first, or fall back to the given location
        Location targetLocation = context.get(LootContextParams.LOOTED_ENTITY).map(livingEntity -> livingEntity.getLocation().add(0, livingEntity.getHeight() / 2, 0))
                .orElseGet(() -> context.getLootedBlockInfo().map(block -> block.getLocation().add(0.5, 0.5, 0.5))
                .orElse(location));

        this.particleSpawnData.trigger(context.getLootingPlayer().orElse(null), targetLocation, context);
    }

    public static ParticleLootItem fromSection(ConfigurationSection section) {
        String particleString = section.getString("particle");
        if (particleString == null)
            return null;

        Particle particle = Arrays.stream(Particle.values()).filter(x -> x.name().equalsIgnoreCase(particleString)).findFirst().orElse(null);
        if (particle == null)
            return null;

        boolean playerOnly = section.getBoolean("player-only", false);
        NumberProvider amount = NumberProvider.fromSection(section, "amount", 1);
        NumberProvider offsetX = NumberProvider.fromSection(section, "offset-x", 0.0);
        NumberProvider offsetY = NumberProvider.fromSection(section, "offset-y", 0.0);
        NumberProvider offsetZ = NumberProvider.fromSection(section, "offset-z", 0.0);
        NumberProvider extra = NumberProvider.fromSection(section, "extra", 0.0);
        boolean longDistance = section.getBoolean("long-distance", false);

        String dataType = particle.getDataType().getSimpleName();
        ParticleDataContainer data = switch (dataType) {
            case "DustOptions" -> new DustOptionsContainer(section);
            case "ItemStack" -> new ItemStackContainer(section);
            case "BlockData" -> new BlockDataContainer(section);
            case "DustTransition" -> new DustTransitionContainer(section);
            case "Vibration" -> new VibrationContainer(section);
            case "Integer" -> new IntegerContainer(section);
            case "Float" -> new FloatContainer(section);
            default -> null;
        };

        return new ParticleLootItem(new ParticleSpawnData(playerOnly, particle, amount, offsetX, offsetY, offsetZ, extra, longDistance, data));
    }

    public static class ParticleSpawnData {

        private final boolean playerOnly;
        private final Particle particle;
        private final NumberProvider amountProvider, offsetXProvider, offsetYProvider, offsetZProvider, extraProvider;
        private final boolean longDistance;
        private final ParticleDataContainer dataContainer;

        public ParticleSpawnData(boolean playerOnly, Particle particle, NumberProvider amount, NumberProvider offsetX, NumberProvider offsetY, NumberProvider offsetZ, NumberProvider extra, boolean longDistance, ParticleDataContainer dataContainer) {
            this.playerOnly = playerOnly;
            this.particle = particle;
            this.amountProvider = amount;
            this.offsetXProvider = offsetX;
            this.offsetYProvider = offsetY;
            this.offsetZProvider = offsetZ;
            this.extraProvider = extra;
            this.longDistance = longDistance;
            this.dataContainer = dataContainer;
        }

        public void trigger(Player player, Location location, LootContext context) {
            int amount = this.amountProvider.getInteger(context);
            double offsetX = this.offsetXProvider.getDouble(context);
            double offsetY = this.offsetYProvider.getDouble(context);
            double offsetZ = this.offsetZProvider.getDouble(context);
            double extra = this.extraProvider.getDouble(context);

            if (this.playerOnly) {
                if (player != null)
                    player.spawnParticle(this.particle, location, amount, offsetX, offsetY, offsetZ, extra, this.dataContainer == null ? null : this.dataContainer.buildData(location, context));
            } else {
                location.getWorld().spawnParticle(this.particle, location, amount, offsetX, offsetY, offsetZ, extra, this.dataContainer == null ? null : this.dataContainer.buildData(location, context), this.longDistance);
            }
        }

    }

    private interface ParticleDataContainer {

        Object buildData(Location location, LootContext context);

    }

    private static class DustOptionsContainer implements ParticleDataContainer {

        protected final NumberProvider red, green, blue, size;

        public DustOptionsContainer(ConfigurationSection section) {
            this.red = NumberProvider.fromSection(section, "red", 0);
            this.green = NumberProvider.fromSection(section, "green", 0);
            this.blue = NumberProvider.fromSection(section, "blue", 0);
            this.size = NumberProvider.fromSection(section, "size", 1.0);
        }

        @Override
        public Object buildData(Location location, LootContext context) {
            int r = LootUtils.clamp(this.red.getInteger(context), 0, 255);
            int g = LootUtils.clamp(this.green.getInteger(context), 0, 255);
            int b = LootUtils.clamp(this.blue.getInteger(context), 0, 255);

            return new Particle.DustOptions(Color.fromRGB(r, g, b), (float) this.size.getDouble(context));
        }

    }

    public static class DustTransitionContainer extends DustOptionsContainer {

        private final NumberProvider redFade, greenFade, blueFade;

        public DustTransitionContainer(ConfigurationSection section) {
            super(section);
            this.redFade = NumberProvider.fromSection(section, "red-fade", 255);
            this.greenFade = NumberProvider.fromSection(section, "green-fade", 255);
            this.blueFade = NumberProvider.fromSection(section, "blue-fade", 255);
        }

        @Override
        public Object buildData(Location location, LootContext context) {
            int r = LootUtils.clamp(this.red.getInteger(context), 0, 255);
            int g = LootUtils.clamp(this.green.getInteger(context), 0, 255);
            int b = LootUtils.clamp(this.blue.getInteger(context), 0, 255);
            int r2 = LootUtils.clamp(this.redFade.getInteger(context), 0, 255);
            int g2 = LootUtils.clamp(this.greenFade.getInteger(context), 0, 255);
            int b2 = LootUtils.clamp(this.blueFade.getInteger(context), 0, 255);

            return new Particle.DustTransition(Color.fromRGB(r, g, b), Color.fromRGB(r2, g2, b2), (float) this.size.getDouble(context));
        }

    }

    private static class ItemStackContainer implements ParticleDataContainer {

        protected final Material material;

        public ItemStackContainer(ConfigurationSection section) {
            this.material = Material.matchMaterial(section.getString("material", Material.STONE.name()));
        }

        @Override
        public Object buildData(Location location, LootContext context) {
            return new ItemStack(this.material);
        }

    }

    private static class BlockDataContainer extends ItemStackContainer {

        public BlockDataContainer(ConfigurationSection section) {
            super(section);
        }

        @Override
        public Object buildData(Location location, LootContext context) {
            return this.material.createBlockData();
        }

    }

    @SuppressWarnings("removal")
    private static class VibrationContainer implements ParticleDataContainer {

        private final NumberProvider time;

        public VibrationContainer(ConfigurationSection section) {
            this.time = NumberProvider.fromSection(section, "time", 20);
        }

        @Override
        public Object buildData(Location location, LootContext context) {
            return new Vibration(location, new Vibration.Destination.BlockDestination(location.getBlock()), Math.max(this.time.getInteger(context), 1));
        }

    }

    private static class IntegerContainer implements ParticleDataContainer {

        private final NumberProvider value;

        public IntegerContainer(ConfigurationSection section) {
            this.value = NumberProvider.fromSection(section, "delay", 0);
        }

        @Override
        public Object buildData(Location location, LootContext context) {
            return this.value.getInteger(context);
        }

    }

    private static class FloatContainer implements ParticleDataContainer {

        private final NumberProvider value;

        public FloatContainer(ConfigurationSection section) {
            this.value = NumberProvider.fromSection(section, "rotation", 0);
        }

        @Override
        public Object buildData(Location location, LootContext context) {
            return (float) this.value.getDouble(context);
        }

    }

}
