package dev.rosewood.roseloot.hook;

import dev.rosewood.roseloot.RoseLoot;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.experience.Profession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MMOCoreHook {

    private static Boolean enabled;
    private static MMOCoreAPI api;

    public static boolean isEnabled() {
        if (enabled != null)
            return enabled;

        if (Bukkit.getPluginManager().getPlugin("MMOCore") != null) {
            api = new MMOCoreAPI(RoseLoot.getInstance());
            enabled = true;
        } else {
            enabled = false;
        }
        return enabled;
    }

    public static void giveExperience(Player player, String profession, double amount) {
        if (!isEnabled())
            return;

        if (profession == null) {
            api.getPlayerData(player).giveExperience(amount, EXPSource.COMMAND);
            return;
        }

        Profession type = MMOCore.plugin.professionManager.get(profession);
        if (type != null) {
            type.giveExperience(api.getPlayerData(player), amount, null, EXPSource.COMMAND);
        } else {
            RoseLoot.getInstance().getLogger().warning("Invalid MMOCore Profession: " + profession);
        }
    }

}
