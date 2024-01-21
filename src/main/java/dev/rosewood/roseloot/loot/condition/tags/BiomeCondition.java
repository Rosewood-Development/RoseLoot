package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

public class BiomeCondition extends BaseLootCondition {

    private List<Biome> biomes;

    public BiomeCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        return context.get(LootContextParams.ORIGIN)
                .map(Location::getBlock)
                .map(Block::getBiome)
                .filter(this.biomes::contains)
                .isPresent();
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
