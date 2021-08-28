package dev.rosewood.roseloot;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.listener.BlockListener;
import dev.rosewood.roseloot.listener.EntityListener;
import dev.rosewood.roseloot.listener.FishingListener;
import dev.rosewood.roseloot.listener.LootGenerateListener;
import dev.rosewood.roseloot.listener.PiglinBarterListener;
import dev.rosewood.roseloot.manager.CommandManager;
import dev.rosewood.roseloot.manager.ConfigurationManager;
import dev.rosewood.roseloot.manager.LocaleManager;
import dev.rosewood.roseloot.manager.LootTableManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
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
        super(-1, 12626, ConfigurationManager.class, null, LocaleManager.class);

        instance = this;
    }

    @Override
    public void enable() {
        if (NMSUtil.getVersionNumber() < 14)
            this.getLogger().severe(this.getDescription().getName() + " only supports 1.14.4 and newer. You will get no support for running an unsupported version.");

        PluginCommand command = this.getCommand("rl");
        if (command != null)
            command.setExecutor(this.getManager(CommandManager.class));

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new BlockListener(this), this);
        pluginManager.registerEvents(new EntityListener(this), this);
        pluginManager.registerEvents(new FishingListener(this), this);
        if (NMSUtil.getVersionNumber() >= 15)
            pluginManager.registerEvents(new LootGenerateListener(this), this);

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
                CommandManager.class,
                LootTableManager.class
        );
    }

    @Override
    public List<Class<? extends DataMigration>> getDataMigrations() {
        return Collections.emptyList();
    }

}
