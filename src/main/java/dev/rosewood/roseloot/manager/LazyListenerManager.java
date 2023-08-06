package dev.rosewood.roseloot.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.hook.ItemsAdderHook;
import dev.rosewood.roseloot.hook.RoseStackerHook;
import dev.rosewood.roseloot.listener.AdvancementListener;
import dev.rosewood.roseloot.listener.ArchaeologyLootGenerateListener;
import dev.rosewood.roseloot.listener.BlockListener;
import dev.rosewood.roseloot.listener.EntityListener;
import dev.rosewood.roseloot.listener.FishingListener;
import dev.rosewood.roseloot.listener.HarvestBlockListener;
import dev.rosewood.roseloot.listener.LootGenerateListener;
import dev.rosewood.roseloot.listener.PiglinBarterListener;
import dev.rosewood.roseloot.listener.helper.LazyListener;
import dev.rosewood.roseloot.listener.hook.ItemsAdderBlockBreakListener;
import dev.rosewood.roseloot.listener.hook.RoseStackerEntityDeathListener;
import dev.rosewood.roseloot.listener.paper.NewerPaperListener;
import dev.rosewood.roseloot.listener.paper.PaperListener;
import java.util.ArrayList;
import java.util.List;

public class LazyListenerManager extends Manager {

    private final List<LazyListener> lazyListeners;

    public LazyListenerManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.lazyListeners = new ArrayList<>();

        this.lazyListeners.add(new BlockListener(rosePlugin));
        this.lazyListeners.add(new EntityListener(rosePlugin));
        this.lazyListeners.add(new FishingListener(rosePlugin));
        this.lazyListeners.add(new AdvancementListener(rosePlugin));
        if (NMSUtil.getVersionNumber() >= 15)
            this.lazyListeners.add(new LootGenerateListener(rosePlugin));
        if (NMSUtil.getVersionNumber() >= 16)
            this.lazyListeners.add(new HarvestBlockListener(rosePlugin));
        if (NMSUtil.isPaper()) {
            this.lazyListeners.add(new PaperListener(rosePlugin));
            if (NMSUtil.getVersionNumber() >= 17)
                this.lazyListeners.add(new NewerPaperListener(rosePlugin));
        }
        if (NMSUtil.getVersionNumber() >= 20)
            this.lazyListeners.add(new ArchaeologyLootGenerateListener(rosePlugin));
        if (RoseStackerHook.isEnabled())
            this.lazyListeners.add(new RoseStackerEntityDeathListener(rosePlugin));
        if (ItemsAdderHook.isEnabled())
            this.lazyListeners.add(new ItemsAdderBlockBreakListener(rosePlugin));

        try {
            // PiglinBarterEvent was added to the 1.16.5 API right before 1.17 was released,
            // so we need an additional check to make sure the class exists
            if (NMSUtil.getVersionNumber() >= 16) {
                Class.forName("org.bukkit.event.entity.PiglinBarterEvent");
                this.lazyListeners.add(new PiglinBarterListener(rosePlugin));
            }
        } catch (Exception e) {
            rosePlugin.getLogger().warning("Your Spigot API version appears to be outdated! Piglin bartering loot tables will be unavailable until you update to the latest API version for your server.");
        }
    }

    @Override
    public void reload() {
        this.lazyListeners.forEach(LazyListener::enable);
    }

    @Override
    public void disable() {
        this.lazyListeners.forEach(LazyListener::disable);
    }

}
