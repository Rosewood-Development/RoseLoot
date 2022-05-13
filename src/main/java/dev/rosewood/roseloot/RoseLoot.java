package dev.rosewood.roseloot;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.hook.RoseStackerHook;
import dev.rosewood.roseloot.hook.conditions.HookConditionListener;
import dev.rosewood.roseloot.listener.AdvancementListener;
import dev.rosewood.roseloot.listener.BlockListener;
import dev.rosewood.roseloot.listener.EntityListener;
import dev.rosewood.roseloot.listener.FireworkDamageListener;
import dev.rosewood.roseloot.listener.FishingListener;
import dev.rosewood.roseloot.listener.LootGenerateListener;
import dev.rosewood.roseloot.listener.PiglinBarterListener;
import dev.rosewood.roseloot.listener.RoseStackerEntityDeathListener;
import dev.rosewood.roseloot.listener.VoucherListener;
import dev.rosewood.roseloot.manager.CommandManager;
import dev.rosewood.roseloot.manager.ConfigurationManager;
import dev.rosewood.roseloot.manager.LocaleManager;
import dev.rosewood.roseloot.manager.LootConditionManager;
import dev.rosewood.roseloot.manager.LootTableManager;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

/**
 * @author Esophose
 */
public class RoseLoot extends RosePlugin {

    /**
     * The running instance of RoseLoot on the server
     */
    private static RoseLoot instance;

    public static RoseLoot getInstance() {
        return instance;
    }

    public RoseLoot() {
        super(101979, 12626, ConfigurationManager.class, null, LocaleManager.class, CommandManager.class);

        instance = this;
    }

    @Override
    public void enable() {
        if (NMSUtil.getVersionNumber() < 16)
            this.getLogger().severe(this.getDescription().getName() + " best supports 1.16.5 servers and newer. If you try to use part of the plugin that is not available for your current server version, expect to see some errors.");

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new BlockListener(this), this);
        pluginManager.registerEvents(new EntityListener(this), this);
        pluginManager.registerEvents(new FishingListener(this), this);
        pluginManager.registerEvents(new AdvancementListener(this), this);
        pluginManager.registerEvents(new VoucherListener(this), this);
        pluginManager.registerEvents(new HookConditionListener(), this);
        pluginManager.registerEvents(new FireworkDamageListener(), this);
        if (NMSUtil.getVersionNumber() >= 15)
            pluginManager.registerEvents(new LootGenerateListener(this), this);
        if (RoseStackerHook.isEnabled())
            pluginManager.registerEvents(new RoseStackerEntityDeathListener(this), this);

        try {
            // PiglinBarterEvent was added to the 1.16.5 API right before 1.17 was released,
            // so we need an additional check to make sure the class exists
            if (NMSUtil.getVersionNumber() >= 16) {
                Class.forName("org.bukkit.event.entity.PiglinBarterEvent");
                pluginManager.registerEvents(new PiglinBarterListener(this), this);
            }
        } catch (Exception e) {
            this.getLogger().warning("Your Spigot API version appears to be outdated! Piglin bartering loot tables will be unavailable until you update to the latest API version for your server.");
        }
    }

    @Override
    public void disable() {

    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return Arrays.asList(
                LootConditionManager.class,
                LootTableManager.class
        );
    }

}
