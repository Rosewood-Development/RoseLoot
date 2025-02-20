package dev.rosewood.roseloot.listener.helper;

import dev.rosewood.rosegarden.RosePlugin;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class LazyListener implements Listener {

    protected final RosePlugin rosePlugin;
    private final Supplier<Boolean> enabledSupplier;
    private boolean enabled;

    public LazyListener(RosePlugin rosePlugin, Supplier<Boolean> enabledSupplier) {
        this.rosePlugin = rosePlugin;
        this.enabledSupplier = enabledSupplier;
    }

    public void enable() {
        this.enabled = this.enabledSupplier.get();
        if (this.enabled)
            Bukkit.getPluginManager().registerEvents(this, this.rosePlugin);
    }

    public void disable() {
        if (this.enabled)
            HandlerList.unregisterAll(this);
        this.enabled = false;
    }

}
