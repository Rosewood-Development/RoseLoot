package dev.rosewood.roseloot.hook;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

public class CoreProtectRecentBlockHook {

    private static final Cache<BlockLocation, Boolean> CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build();
    private static Boolean enabled;

    public static boolean isEnabled() {
        if (enabled != null)
            return enabled;
        return enabled = Bukkit.getPluginManager().getPlugin("CoreProtect") != null;
    }

    public static void markBlock(Block block) {
        if (isEnabled())
            CACHE.put(BlockLocation.of(block), true);
    }

    public static boolean isMarked(Block block) {
        if (!isEnabled())
            return false;
        return CACHE.getIfPresent(BlockLocation.of(block)) != null;
    }

    private record BlockLocation(String world, int x, int y, int z) {
        public static BlockLocation of(Block block) {
            return new BlockLocation(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
        }
    }

}
