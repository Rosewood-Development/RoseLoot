package dev.rosewood.roseloot.loot.item;

import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.DecoratedPot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ContainerContentsLootItem implements ItemGenerativeLootItem {

    @Override
    public List<ItemStack> generate(LootContext context) {
        List<ItemStack> droppedContents = new ArrayList<>();
        Optional<Block> lootedBlock = context.get(LootContextParams.LOOTED_BLOCK);
        if (lootedBlock.isEmpty())
            return droppedContents;

        BlockState blockState = lootedBlock.get().getState();
        if (blockState instanceof Container container) {
            droppedContents.addAll(Arrays.stream(container.getInventory().getContents()).filter(Objects::nonNull).toList());
        } else if (NMSUtil.getVersionNumber() >= 20 && blockState instanceof DecoratedPot decoratedPot) {
            droppedContents.addAll(decoratedPot.getShards().stream().map(ItemStack::new).toList());
        }

        return droppedContents;
    }

    @Override
    public List<ItemStack> getAllItems(LootContext context) {
        return this.generate(context);
    }

    public static ContainerContentsLootItem fromSection(ConfigurationSection section) {
        return new ContainerContentsLootItem();
    }

}
