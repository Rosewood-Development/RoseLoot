package dev.rosewood.roseloot.loot.item.component;

import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.RoseLoot;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ComponentMappings {

    public static final Map<String, Function<ConfigurationSection, ? extends LootItemComponent>> CONSTRUCTORS;
    public static final Map<String, BiConsumer<ItemStack, StringBuilder>> PROPERTY_APPLIERS;

    static {
        CONSTRUCTORS = new HashMap<>();
        PROPERTY_APPLIERS = new HashMap<>();

        if (NMSUtil.isPaper()) {
            try {
                String name = null;
                int major = NMSUtil.getVersionNumber();
                int minor = NMSUtil.getMinorVersionNumber();
                if (major == 21 && minor == 4) {
                    name = "v1_21_3";
                } else if (major == 21 && minor == 5) {
                    name = "v1_21_4";
                } else if (major == 21 && (minor == 6 || minor == 7 || minor == 8)) {
                    name = "v1_21_5";
                } else if (major == 21 && (minor == 9 || minor == 10)) {
                    name = "v1_21_6";
                } else if (major == 21 && minor == 11) {
                    name = "v1_21_7";
                } else {
                    RoseLoot.getInstance().getLogger().warning("Components are not available for this version");
                }

                if (name != null) {
                    LootItemComponentProvider provider = (LootItemComponentProvider) Class.forName("dev.rosewood.roseloot.loot.item.component." + name + ".LootItemComponentProviderImpl").getConstructor().newInstance();
                    CONSTRUCTORS.putAll(provider.provideLootItemComponentConstructors());
                    PROPERTY_APPLIERS.putAll(provider.provideLootItemComponentPropertyApplicators());

                    if (!CONSTRUCTORS.keySet().equals(PROPERTY_APPLIERS.keySet()))
                        throw new IllegalStateException("Mismatch between LootItemComponentProvider values: " + name);

                    RoseLoot.getInstance().getLogger().info("Loaded " + CONSTRUCTORS.size() + " loot item components");
                }
            } catch (Exception ignored) { }
        }
    }

}
