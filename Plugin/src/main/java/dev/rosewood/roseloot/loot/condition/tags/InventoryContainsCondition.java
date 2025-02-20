package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.condition.BaseLootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.Optional;
import java.util.function.BiFunction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryContainsCondition extends BaseLootCondition {

    private final BiFunction<LootContext, String, ItemStack> itemFunction;
    private final String source;
    private String materialName;
    private int amount;

    public InventoryContainsCondition(String tag) {
        super(tag);
        this.itemFunction = (context, name) -> {
            Material material = Material.matchMaterial(name);
            if (material == null)
                return null;
            return new ItemStack(material);
        };
        this.source = "vanilla";
    }

    public InventoryContainsCondition(String tag, BiFunction<LootContext, String, ItemStack> itemFunction, String source) {
        super(tag);
        this.itemFunction = itemFunction;
        this.source = source;
    }

    @Override
    public boolean check(LootContext context) {
        Optional<Player> playerOptional = context.getLootingPlayer();
        if (playerOptional.isEmpty())
            return false;

        Player player = playerOptional.get();
        ItemStack itemStack = this.itemFunction.apply(context, this.materialName);
        if (itemStack == null) {
            RoseLoot.getInstance().getLogger().warning("Could not resolve " + this.source + "item with id " + this.materialName);
            return false;
        }

        return player.getInventory().containsAtLeast(itemStack, this.amount);
    }

    @Override
    protected boolean parseValues(String[] values) {
        if (values.length == 1) {
            this.materialName = values[0];
            this.amount = 1;
            return true;
        } else if (values.length == 2) {
            this.materialName = values[0];
            try {
                this.amount = Integer.parseInt(values[1]);
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
        return false;
    }

}
