package dev.rosewood.roseloot;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.SettingHolder;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.config.SettingKey;
import dev.rosewood.roseloot.hook.conditions.HookConditionListener;
import dev.rosewood.roseloot.listener.FireworkDamageListener;
import dev.rosewood.roseloot.listener.ItemPickupListener;
import dev.rosewood.roseloot.listener.VoucherListener;
import dev.rosewood.roseloot.manager.CommandManager;
import dev.rosewood.roseloot.manager.CooldownManager;
import dev.rosewood.roseloot.manager.DataManager;
import dev.rosewood.roseloot.manager.LazyListenerManager;
import dev.rosewood.roseloot.manager.LocaleManager;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.manager.SupportedBlockManager;
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
        super(101979, 12626, DataManager.class, LocaleManager.class, CommandManager.class);

        instance = this;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void enable() {
        if (NMSUtil.getVersionNumber() < 16)
            this.getLogger().severe(this.getDescription().getName() + " best supports 1.16.5 servers and newer. If you try to use part of the plugin that is not available for your current server version, expect to see some errors.");

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new VoucherListener(this), this);
        pluginManager.registerEvents(new HookConditionListener(), this);
        pluginManager.registerEvents(new FireworkDamageListener(this), this);
        pluginManager.registerEvents(new ItemPickupListener(), this);
    }

    @Override
    public void disable() {

    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of(
                LootTableManager.class,
                LazyListenerManager.class,
                CooldownManager.class,
                SupportedBlockManager.class
        );
    }

    @Override
    public boolean isLocalDatabaseOnly() {
        return true;
    }

    @Override
    protected SettingHolder getRoseConfigSettingHolder() {
        return SettingKey.INSTANCE;
    }

    @Override
    protected String[] getRoseConfigHeader() {
        return new String[] {
                "     __________                     ____                  __",
                "     \\______   \\ ____  ______ ____ |    |    ____   _____/  |_",
                "      |       _//  _ \\/  ___// __ \\|    |   /  _ \\ /  _ \\   __\\",
                "      |    |   (  <_> )___ \\\\  ___/|    |__(  <_> |  <_> )  |",
                "      |____|_  /\\____/____  >\\___  >_______ \\____/ \\____/|__|",
                "             \\/           \\/     \\/        \\/"
        };
    }

}
