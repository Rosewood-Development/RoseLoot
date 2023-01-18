package dev.rosewood.roseloot.loot.item;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ContainerContentsLootItem extends ItemLootItem {

    public ContainerContentsLootItem() {
        super();
    }

    @Override
    public List<ItemStack> generate(LootContext context) {
        List<ItemStack> droppedContents = new ArrayList<>();
        Optional<Block> lootedBlock = context.get(LootContextParams.LOOTED_BLOCK);
        if (lootedBlock.isEmpty())
            return droppedContents;

        if (lootedBlock.get().getState() instanceof Container container)
            droppedContents.addAll(Arrays.stream(container.getInventory().getContents()).filter(Objects::nonNull).toList());

        return droppedContents;
    }

    public static ContainerContentsLootItem fromSection(ConfigurationSection section) {
        return new ContainerContentsLootItem();
    }

}
