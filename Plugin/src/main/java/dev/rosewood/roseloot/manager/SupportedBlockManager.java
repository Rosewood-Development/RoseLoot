package dev.rosewood.roseloot.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.scheduler.task.ScheduledTask;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class SupportedBlockManager extends Manager {

    private static final long TICK_MS = 1000 / 20;

    private final Map<Block, Entry> supportedBlockMap;
    private ScheduledTask task;

    public SupportedBlockManager(RosePlugin rosePlugin) {
        super(rosePlugin);
        this.supportedBlockMap = new HashMap<>();
    }

    @Override
    public void reload() {
        this.task = this.rosePlugin.getScheduler().runTaskTimer(this::update, 0L, 1L);
    }

    @Override
    public void disable() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }

    public void handleSupportedBlock(Player player, Block block) {
        this.supportedBlockMap.put(block, new Entry(player, System.currentTimeMillis() + TICK_MS * 2));
    }

    public Player getSupportedBlockBreaker(Block block) {
        List<BlockFace> directionsToCheck;
        if (block.getType() == Material.CHORUS_FLOWER || block.getType() == Material.CHORUS_PLANT) {
            directionsToCheck = List.of(BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
        } else {
            directionsToCheck = List.of(BlockFace.DOWN);
        }

        for (BlockFace direction : directionsToCheck) {
            Block relative = block.getRelative(direction);
            Entry entry = this.supportedBlockMap.get(relative);
            if (entry != null) {
                Player player = entry.player();
                this.handleSupportedBlock(player, block);
                return player;
            }
        }

        return null;
    }

    private void update() {
        long time = System.currentTimeMillis();
        this.supportedBlockMap.entrySet().removeIf(entry -> time >= entry.getValue().expirationTime());
    }

    private record Entry(Player player, long expirationTime) { }

}
