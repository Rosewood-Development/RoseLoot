package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Repairable;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

public class RepairableComponent implements LootItemComponent {

    private final StringProvider repairableTypes;

    public RepairableComponent(ConfigurationSection section) {
        ConfigurationSection repairableSection = section.getConfigurationSection("repairable");
        if (repairableSection != null) {
            this.repairableTypes = StringProvider.fromSection(repairableSection, "types", null);
        } else {
            this.repairableTypes = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.repairableTypes != null) {
            List<String> typeStrings = this.repairableTypes.getList(context);
            Registry<ItemType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);
            if (typeStrings.size() == 1 && typeStrings.getFirst().startsWith("#")) {
                String tag = typeStrings.getFirst().toLowerCase();
                TagKey<ItemType> tagKey = TagKey.create(RegistryKey.ITEM, Key.key(tag.substring(1)));
                itemStack.setData(DataComponentTypes.REPAIRABLE, Repairable.repairable(registry.getTag(tagKey)));
            } else {
                List<ItemType> itemTypes = new ArrayList<>();
                for (String value : typeStrings) {
                    if (value.startsWith("#")) {
                        TagKey<ItemType> tagKey = TagKey.create(RegistryKey.ITEM, Key.key(value.substring(1)));
                        Tag<ItemType> tag = registry.getTag(tagKey);
                        itemTypes.addAll(tag.resolve(registry));
                    } else {
                        Key key = Key.key(value.toLowerCase());
                        ItemType itemType = registry.get(key);
                        if (itemType != null)
                            itemTypes.add(itemType);
                    }
                }
                itemStack.setData(DataComponentTypes.REPAIRABLE, Repairable.repairable(RegistrySet.keySetFromValues(RegistryKey.ITEM, itemTypes)));
            }
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.REPAIRABLE))
            return;

        Repairable repairable = itemStack.getData(DataComponentTypes.REPAIRABLE);
        stringBuilder.append("repairable:\n");
        
        if (repairable.types() instanceof io.papermc.paper.registry.tag.Tag<?> tag) {
            stringBuilder.append("  types: '#").append(tag.tagKey().key().asMinimalString()).append("'\n");
        } else {
            stringBuilder.append("  types:\n");
            for (TypedKey<ItemType> key : repairable.types().values())
                stringBuilder.append("    - '").append(key.asMinimalString()).append("'\n");
        }
    }
} 
