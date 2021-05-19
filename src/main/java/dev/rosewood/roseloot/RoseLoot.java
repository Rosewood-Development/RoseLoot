package dev.rosewood.roseloot;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.listener.BlockListener;
import dev.rosewood.roseloot.listener.EntityListener;
import dev.rosewood.roseloot.manager.CommandManager;
import dev.rosewood.roseloot.manager.ConfigurationManager;
import dev.rosewood.roseloot.manager.LocaleManager;
import dev.rosewood.roseloot.manager.LootTableManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
        super(-1, -1, ConfigurationManager.class, null, LocaleManager.class);

        instance = this;
    }

    @Override
    public void enable() {
        if (NMSUtil.getVersionNumber() < 13)
            this.getLogger().severe(this.getDescription().getName() + " only supports 1.13.2 and newer. You will get no support for running an unsupported version.");

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new BlockListener(this), this);
        pluginManager.registerEvents(new EntityListener(this), this);
    }

    @Override
    public void disable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("rl"))
            return true;

        this.reload();
        this.getManager(LocaleManager.class).sendCustomMessage(sender, "&aReloaded plugin.");
        return true;
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
