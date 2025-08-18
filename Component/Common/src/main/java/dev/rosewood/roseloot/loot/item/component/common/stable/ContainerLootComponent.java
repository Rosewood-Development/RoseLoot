package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SeededContainerLoot;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ContainerLootComponent implements LootItemComponent {

    private final StringProvider lootTableKey;
    private final NumberProvider seed;

    public ContainerLootComponent(ConfigurationSection section) {
        ConfigurationSection lootSection = section.getConfigurationSection("container-loot");
        if (lootSection != null) {
            this.lootTableKey = StringProvider.fromSection(lootSection, "loot-table", null);
            this.seed = NumberProvider.fromSection(lootSection, "seed", null);
        } else {
            this.lootTableKey = null;
            this.seed = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.lootTableKey != null) {
            Key lootTableKey = Key.key(this.lootTableKey.get(context).toLowerCase());
            SeededContainerLoot.Builder builder = SeededContainerLoot.seededContainerLoot(lootTableKey);

            if (this.seed != null)
                builder.seed((long) this.seed.getDouble(context));

            itemStack.setData(DataComponentTypes.CONTAINER_LOOT, builder.build());
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.CONTAINER_LOOT))
            return;

        SeededContainerLoot loot = itemStack.getData(DataComponentTypes.CONTAINER_LOOT);
        stringBuilder.append("container-loot:\n");
        stringBuilder.append("  loot-table: ").append(loot.lootTable().asMinimalString()).append('\n');
        stringBuilder.append("  seed: ").append(loot.seed()).append('\n');
    }

} 
