package dev.rosewood.roseloot.loot.item.component;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public interface LootItemComponentProvider {

    Map<String, Function<ConfigurationSection, LootItemComponent>> provideLootItemComponentConstructors();

    Map<String, BiConsumer<ItemStack, StringBuilder>> provideLootItemComponentPropertyApplicators();

}
