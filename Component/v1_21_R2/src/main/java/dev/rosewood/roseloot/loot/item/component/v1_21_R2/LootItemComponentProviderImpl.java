package dev.rosewood.roseloot.loot.item.component.v1_21_R2;

import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.loot.item.component.LootItemComponentProvider;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class LootItemComponentProviderImpl implements LootItemComponentProvider {

    @Override
    public Map<String, Function<ConfigurationSection, LootItemComponent>> provideLootItemComponentConstructors() {
        return Map.ofEntries(

        );
    }

    @Override
    public Map<String, BiConsumer<ItemStack, StringBuilder>> provideLootItemComponentPropertyApplicators() {
        return Map.ofEntries(

        );
    }

}
