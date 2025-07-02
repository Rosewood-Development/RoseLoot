package dev.rosewood.roseloot.config;

import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.config.SettingHolder;
import dev.rosewood.rosegarden.config.SettingSerializers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Material;

public final class SettingKey implements SettingHolder {

    private static final List<RoseSetting<?>> KEYS = new ArrayList<>();
    public static final SettingKey INSTANCE = new SettingKey();

    public static final RoseSetting<List<String>> DISABLED_WORLDS = create(RoseSetting.ofStringList("disabled-worlds", List.of("disabled_world_name"), "A list of worlds that the plugin is disabled in"));
    public static final RoseSetting<Boolean> ALLOW_BLOCK_EXPLOSION_LOOT = create(RoseSetting.ofBoolean("allow-block-explosion-loot", false, "If enabled, blocks destroyed by explosions will run loot tables", "You can use the condition 'explosion' to check for an explosion", "Avoid using item based conditions for explosions, you may get unexpected results", "WARNING: Do not trigger an explosion from a loot table triggered by an explosion.", "         It will likely cause an infinite loop and crash your server."));
    public static final RoseSetting<List<Material>> PIGLIN_BARTER_ITEMS = create(RoseSetting.ofValue("piglin-barter-items", SettingSerializers.MATERIAL_LIST, List.of(Material.GOLD_INGOT), "A list of items that piglins will pick up for bartering", "This requires at least one bartering loot table to be enabled"));
    public static final RoseSetting<Boolean> SIMULATE_BLOCKDROPITEMEVENT = create(RoseSetting.ofBoolean("simulate-blockdropitemevent", false, "Should the BlockBreakItemEvent be simulated for custom item drops from blocks?", "May be required for some plugins that add items to the player's inventory automatically", "This may cause issues with some stacker plugins"));
    public static final RoseSetting<Boolean> SIMULATE_LEAVESDECAYEVENT = create(RoseSetting.ofBoolean("simulate-leavesdecayevent", false, "Should the LeavesDecayEvent be simulated for custom item drops from leaves?", "May be required for some plugins that listen to the event"));
    public static final RoseSetting<Boolean> CALL_POSTLOOTGENERATEEVENT = create(RoseSetting.ofBoolean("call-postlootgenerateevent", false, "Should the PostLootGenerateEvent be called after loot tables are run?", "You may need to enable this if you're using a plugin that uses the RoseLoot API"));
    public static final RoseSetting<Boolean> LOG_LOOT_TABLE_WARNINGS = create(RoseSetting.ofBoolean("log-loot-table-warnings", true, "Should loot table warnings that are printed to console also be printed to", "player chat when running the plugin reload command?"));

    private static <T> RoseSetting<T> create(RoseSetting<T> setting) {
        KEYS.add(setting);
        return setting;
    }

    public static List<RoseSetting<?>> getKeys() {
        return Collections.unmodifiableList(KEYS);
    }

    private SettingKey() {}

    @Override
    public List<RoseSetting<?>> get() {
        return KEYS;
    }

}
