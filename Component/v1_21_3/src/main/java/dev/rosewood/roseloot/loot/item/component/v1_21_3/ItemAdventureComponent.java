package dev.rosewood.roseloot.loot.item.component.v1_21_3;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.block.BlockPredicate;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.item.ItemAdventurePredicate;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

abstract class ItemAdventureComponent implements LootItemComponent {

    private final DataComponentType.Valued<ItemAdventurePredicate> dataComponentType;
    private final StringProvider predicates;
    private final Boolean showInTooltip;

    public ItemAdventureComponent(DataComponentType.Valued<ItemAdventurePredicate> dataComponentType, String configKey, ConfigurationSection section) {
        this.dataComponentType = dataComponentType;

        ConfigurationSection canPlaceOnSection = section.getConfigurationSection(configKey);
        if (canPlaceOnSection != null) {
            this.predicates = StringProvider.fromSection(canPlaceOnSection, "predicates", null);
            if (canPlaceOnSection.isBoolean("show-in-tooltip")) {
                this.showInTooltip = canPlaceOnSection.getBoolean("show-in-tooltip");
            } else {
                this.showInTooltip = null;
            }
        } else {
            this.predicates = null;
            this.showInTooltip = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        ItemAdventurePredicate.Builder builder = ItemAdventurePredicate.itemAdventurePredicate();

        if (this.predicates != null) {
            List<String> predicateStrings = this.predicates.getList(context);
            List<BlockPredicate> predicates = new ArrayList<>(predicateStrings.size());
            Registry<BlockType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BLOCK);
            for (String predicateString : predicateStrings) {
                RegistryKeySet<BlockType> registryKeySet;
                if (predicateString.startsWith("#")) {
                    TagKey<BlockType> tagKey = TagKey.create(RegistryKey.BLOCK, predicateString.substring(1));
                    registryKeySet = registry.getTag(tagKey);
                } else {
                    Key key = Key.key(predicateString);
                    BlockType blockType = registry.get(key);
                    registryKeySet = RegistrySet.keySetFromValues(RegistryKey.BLOCK, List.of(blockType));
                }
                predicates.add(BlockPredicate.predicate().blocks(registryKeySet).build());
            }
            builder.addPredicates(predicates);
        }

        if (this.showInTooltip != null)
            builder.showInTooltip(this.showInTooltip);

        itemStack.setData(this.dataComponentType, builder.build());
    }

    public static void applyProperties(DataComponentType.Valued<ItemAdventurePredicate> dataComponentType, String configKey, ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(dataComponentType))
            return;

        ItemAdventurePredicate itemAdventurePredicate = itemStack.getData(dataComponentType);
        List<RegistryKeySet<BlockType>> predicates = itemAdventurePredicate.predicates().stream()
                .map(BlockPredicate::blocks)
                .filter(Objects::nonNull)
                .filter(x -> !x.isEmpty())
                .toList();
        if (!predicates.isEmpty()) {
            stringBuilder.append(configKey).append(":\n");
            stringBuilder.append("  predicates:\n");
            for (RegistryKeySet<BlockType> keySet : predicates) {
                if (keySet instanceof Tag<?> tag) {
                    String name = tag.tagKey().key().asMinimalString();
                    stringBuilder.append("    - '#").append(name).append("'\n");
                } else {
                    for (TypedKey<BlockType> typedKey : keySet.values()) {
                        String name = typedKey.key().asMinimalString();
                        stringBuilder.append("    - '").append(name).append("'\n");
                    }
                }
            }
            stringBuilder.append("  show-in-tooltip: ").append(itemAdventurePredicate.showInTooltip()).append('\n');
        }
    }

}
