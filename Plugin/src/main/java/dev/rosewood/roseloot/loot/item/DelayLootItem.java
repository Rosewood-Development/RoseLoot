package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.manager.LootTableManager;
import dev.rosewood.roseloot.provider.NumberProvider;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class DelayLootItem implements TriggerableLootItem {

    private final NumberProvider delay;
    private final List<LootItem> lootItems;

    protected DelayLootItem(NumberProvider delay, List<LootItem> lootItems) {
        this.delay = delay;
        this.lootItems = lootItems;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        int delay = Math.max(0, this.delay.getInteger(context));
        LootContents contents = new LootContents(context);
        contents.add(this.lootItems);
        RoseLoot.getInstance().getScheduler().runTaskLater(() -> contents.dropAtLocation(location), delay);
    }

    public static DelayLootItem fromSection(ConfigurationSection section) {
        NumberProvider delay = NumberProvider.fromSection(section, "value", 1);
        LootTableManager lootTableManager = RoseLoot.getInstance().getManager(LootTableManager.class);
        List<LootItem> lootItems = lootTableManager.parseLootItemsSection("$internal", "$delay", "$delay", section);
        return new DelayLootItem(delay, lootItems);
    }

}
