package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.LootContext;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;

public class InFluidCondition extends LootCondition {

    private List<Fluid> fluids;

    public InFluidCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        Location location = context.getLocation();
        return this.fluids.stream().anyMatch(x -> x.isInFluid(location));
    }

    @Override
    public boolean parseValues(String[] values) {
        this.fluids = new ArrayList<>();

        for (String value : values) {
            try {
                this.fluids.add(Fluid.valueOf(value.toUpperCase()));
            } catch (Exception ignored) { }
        }

        return !this.fluids.isEmpty();
    }

    private enum Fluid {
        WATER {
            @Override
            boolean isInFluid(Location location) {
                Block block = location.getBlock();
                if (block.getType() == Material.WATER)
                    return true;

                BlockData blockData = block.getBlockData();
                if (!(blockData instanceof Waterlogged))
                    return false;

                return ((Waterlogged) blockData).isWaterlogged();
            }
        },
        LAVA {
            @Override
            boolean isInFluid(Location location) {
                return location.getBlock().getType() == Material.LAVA;
            }
        };

        abstract boolean isInFluid(Location location);
    }

}
