package dev.rosewood.roseloot.loot.item.component.latest;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotDecorations;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

class PotDecorationsComponent implements LootItemComponent {

    private final ItemType back;
    private final ItemType left;
    private final ItemType right;
    private final ItemType front;

    public PotDecorationsComponent(ConfigurationSection section) {
        ConfigurationSection decorationsSection = section.getConfigurationSection("pot-decorations");
        if (decorationsSection != null) {
            String backKey = decorationsSection.getString("back");
            String leftKey = decorationsSection.getString("left");
            String rightKey = decorationsSection.getString("right");
            String frontKey = decorationsSection.getString("front");

            Registry<ItemType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);
            this.back = backKey != null ? registry.get(NamespacedKey.fromString(backKey)) : null;
            this.left = leftKey != null ? registry.get(NamespacedKey.fromString(leftKey)) : null;
            this.right = rightKey != null ? registry.get(NamespacedKey.fromString(rightKey)) : null;
            this.front = frontKey != null ? registry.get(NamespacedKey.fromString(frontKey)) : null;
        } else {
            this.back = null;
            this.left = null;
            this.right = null;
            this.front = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.back != null || this.left != null || this.right != null || this.front != null) {
            itemStack.setData(DataComponentTypes.POT_DECORATIONS, PotDecorations.potDecorations(this.back, this.left, this.right, this.front));
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.POT_DECORATIONS))
            return;

        PotDecorations decorations = itemStack.getData(DataComponentTypes.POT_DECORATIONS);

        if (decorations.back() != null || decorations.left() != null || decorations.right() != null || decorations.front() != null) {
            stringBuilder.append("pot-decorations:\n");
        
            if (decorations.back() != null)
                stringBuilder.append("  back: ").append(decorations.back().key().asMinimalString()).append('\n');
            if (decorations.left() != null)
                stringBuilder.append("  left: ").append(decorations.left().key().asMinimalString()).append('\n');
            if (decorations.right() != null)
                stringBuilder.append("  right: ").append(decorations.right().key().asMinimalString()).append('\n');
            if (decorations.front() != null)
                stringBuilder.append("  front: ").append(decorations.front().key().asMinimalString()).append('\n');
        }
    }
} 
