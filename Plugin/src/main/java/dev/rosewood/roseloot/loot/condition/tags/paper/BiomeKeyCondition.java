package dev.rosewood.roseloot.loot.condition.tags.paper;

import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

public class BiomeKeyCondition extends BaseLootCondition {

    private List<NamespacedKey> biomeKeys;
    private static Method getBiomeKeyMethod;

    static {
        try {
            getBiomeKeyMethod = Bukkit.getUnsafe().getClass().getMethod("getBiomeKey", World.class, double.class, double.class, double.class);
        } catch (NoSuchMethodException ignored) { }
    }

    public BiomeKeyCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean check(LootContext context) {
        return context.get(LootContextParams.ORIGIN)
                .map(Location::getBlock)
                .map(x -> {
                    try {
                        if (getBiomeKeyMethod != null) {
                            return (NamespacedKey) getBiomeKeyMethod.invoke(Bukkit.getUnsafe(), x.getWorld(), x.getX(), x.getY(), x.getZ());
                        } else {
                            return x.getBiome().getKey();
                        }
                    } catch (IllegalAccessException | InvocationTargetException ignored) { }
                    return null;
                })
                .filter(this.biomeKeys::contains)
                .isPresent();
    }

    @Override
    public boolean parseValues(String[] values) {
        this.biomeKeys = new ArrayList<>();

        for (String value : values) {
            try {
                this.biomeKeys.add(NamespacedKey.fromString(value));
            } catch (Exception ignored) { }
        }

        return !this.biomeKeys.isEmpty();
    }

    @Override
    protected String getDeprecationReplacement() {
        if (NMSUtil.getVersionNumber() > 21 || NMSUtil.getVersionNumber() == 21 && NMSUtil.getMinorVersionNumber() >= 3)
            return "biome";
        return super.getDeprecationReplacement();
    }

}
