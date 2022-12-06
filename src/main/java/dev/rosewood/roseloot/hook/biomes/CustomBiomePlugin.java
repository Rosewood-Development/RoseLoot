package dev.rosewood.roseloot.hook.biomes;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.util.Lazy;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import org.bukkit.Location;

public enum CustomBiomePlugin {

    TERRA(TerraBiomeProvider::new);

    private final Lazy<BiomeProvider> biomeProvider;

    CustomBiomePlugin(Supplier<BiomeProvider> lazyLoader) {
        this.biomeProvider = new Lazy<>(lazyLoader);
    }

    public boolean isEnabled() {
        return this.biomeProvider.get().isEnabled();
    }

    public String resolveBiome(Location location) {
        return this.biomeProvider.get().getBiomeName(location);
    }

    public BiPredicate<LootContext, List<String>> getLootConditionPredicate() {
        return (context, values) -> context.get(LootContextParams.ORIGIN)
                .map(this::resolveBiome)
                .filter(x -> values.stream().anyMatch(x::equalsIgnoreCase))
                .isPresent();
    }

    public static CustomBiomePlugin fromString(String name) {
        for (CustomBiomePlugin value : values())
            if (value.name().equalsIgnoreCase(name))
                return value;
        return null;
    }

}
