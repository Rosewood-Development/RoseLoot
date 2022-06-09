package dev.rosewood.roseloot.hook.biomes;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.function.BiPredicate;
import org.bukkit.Location;

public enum CustomBiomePlugin {

    TERRA(new TerraBiomeProvider("Terra"));

    private final BiomeProvider biomeProvider;

    CustomBiomePlugin(BiomeProvider biomeProvider) {
        this.biomeProvider = biomeProvider;
    }

    public boolean isEnabled() {
        return this.biomeProvider.isEnabled();
    }

    public String resolveBiome(Location location) {
        return this.biomeProvider.getBiomeName(location);
    }

    public BiPredicate<LootContext, String> getLootConditionPredicate() {
        return (context, value) -> context.get(LootContextParams.ORIGIN)
                .map(this::resolveBiome)
                .filter(x -> x.equalsIgnoreCase(value))
                .isPresent();
    }

    public static CustomBiomePlugin fromString(String name) {
        for (CustomBiomePlugin value : values())
            if (value.name().equalsIgnoreCase(name))
                return value;
        return null;
    }

}
