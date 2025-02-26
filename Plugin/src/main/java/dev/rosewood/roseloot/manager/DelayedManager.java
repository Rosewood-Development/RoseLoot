package dev.rosewood.roseloot.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;

public abstract class DelayedManager extends Manager {

    public DelayedManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public final void reload() {
        this.rosePlugin.getScheduler().runTask(this::delayedReload);
    }

    protected abstract void delayedReload();

}
