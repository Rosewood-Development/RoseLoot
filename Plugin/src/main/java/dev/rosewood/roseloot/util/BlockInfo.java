package dev.rosewood.roseloot.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;

public interface BlockInfo {

    Material getMaterial();

    BlockData getData();

    BlockState getState();

    Location getLocation();

    static BlockInfo of(Block block) {
        return new BlockBlockInfo(block);
    }

    static BlockInfo of(BlockState blockState) {
        return new BlockStateBlockInfo(blockState);
    }

    class BlockBlockInfo implements BlockInfo {
        private final Block block;

        private BlockBlockInfo(Block block) {
            this.block = block;
        }

        @Override
        public Material getMaterial() {
            return this.block.getType();
        }

        @Override
        public BlockData getData() {
            return this.block.getBlockData();
        }

        @Override
        public BlockState getState() {
            return this.block.getState();
        }

        @Override
        public Location getLocation() {
            return this.block.getLocation();
        }
    }

    class BlockStateBlockInfo implements BlockInfo {
        private final BlockState blockState;

        private BlockStateBlockInfo(BlockState blockState) {
            this.blockState = blockState;
        }

        @Override
        public Material getMaterial() {
            return this.blockState.getType();
        }

        @Override
        public BlockData getData() {
            return this.blockState.getBlockData();
        }

        @Override
        public BlockState getState() {
            return this.blockState;
        }

        @Override
        public Location getLocation() {
            return this.blockState.getLocation();
        }
    }

}
