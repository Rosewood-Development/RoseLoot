package dev.rosewood.roseloot.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.Bukkit;

public abstract class DelayedManager extends Manager {

    public DelayedManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public final void reload() {
        Bukkit.getScheduler().runTask(this.rosePlugin, this::delayedReload);
    }

    protected abstract void delayedReload();

}
