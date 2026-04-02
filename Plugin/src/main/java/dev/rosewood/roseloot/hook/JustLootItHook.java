package dev.rosewood.roseloot.hook;

import org.bukkit.Bukkit;

public class JustLootItHook {

    private static Boolean enabled;

    public static boolean isEnabled() {
        if (enabled != null)
            return enabled;
        return enabled = Bukkit.getPluginManager().getPlugin("JustLootIt") != null;
    }

}
