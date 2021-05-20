package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.block.Biome;

public class BiomeCondition extends LootCondition {

    private List<Biome> biomes;

    public BiomeCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        return this.biomes.contains(context.getLocation().getBlock().getBiome());
    }

    @Override
    public boolean parseValues(String[] values) {
        this.biomes = new ArrayList<>();

        for (String value : values) {
            try {
                Biome biome = Biome.valueOf(value.toUpperCase());
                this.biomes.add(biome);
            } catch (Exception ignored) { }
        }

        return !this.biomes.isEmpty();
    }

}
