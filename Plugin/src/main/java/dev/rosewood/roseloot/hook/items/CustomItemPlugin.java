package dev.rosewood.roseloot.hook.items;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.tags.InventoryContainsCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.Lazy;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import org.bukkit.inventory.ItemStack;

public enum CustomItemPlugin {

    ECO(EcoItemProvider::new),
    MMOITEMS(MMOItemProvider::new),
    MYTHICMOBS(MythicMobsItemProvider::new),
    NEIGEITEMS(NeigeItemProvider::new),
    ITEMBRIDGE(ItemBridgeItemProvider::new),
    EXECUTABLEITEMS(ExecutableItemProvider::new),
    EXECUTABLEBLOCKS(ExecutableBlockProvider::new),
    ITEMSADDER(ItemsAdderItemProvider::new),
    ITEMSXL(ItemsXLItemProvider::new),
    ORAXEN(OraxenItemProvider::new),
    KNOKKOCUSTOMITEMS(KnokkoCustomItemProvider::new),
    ITEMEDIT(ItemEditItemProvider::new),
    UBERITEMS(UberItemProvider::new),
    DEEZITEMS(DeezItemsProvider::new),
    SLIMEFUN(SlimefunItemProvider::new),
    CUSTOMCRAFTING(CustomCraftingItemProvider::new),
    CUSTOMFISHING(CustomFishingItemProvider::new),
    ADVANCEDITEMS(AdvancedItemsProvider::new),
    ZITEMS(ZItemProvider::new),
    ZESSENTIALS(ZEssentialsItemProvider::new),
    NEXO(NexoItemProvider::new),
    CRAZYVOUCHERS(CrazyVouchersItemProvider::new),
    CRAFTENGINE(CraftEngineItemProvider::new);

    private final Lazy<ItemProvider> itemProvider;

    CustomItemPlugin(Supplier<ItemProvider> lazyLoader) {
        this.itemProvider = new Lazy<>(lazyLoader);
    }

    public boolean isEnabled() {
        return this.itemProvider.get().isEnabled();
    }

    public ItemStack resolveItem(LootContext context, String id) {
        return this.itemProvider.get().getItem(context, id);
    }

    public Set<String> resolveItemIds(ItemStack itemStack) {
        return this.itemProvider.get().getItemIds(itemStack);
    }

    public boolean supportsIdLookup() {
        return this.itemProvider.get().supportsIdLookup();
    }

    public String getConditionSuffix() {
        return this.itemProvider.get().getConditionSuffix();
    }

    public BiPredicate<LootContext, List<String>> getInHandLootConditionPredicate() {
        return (context, values) -> {
            Optional<ItemStack> itemUsed = context.getItemUsed();
            if (itemUsed.isEmpty())
                return false;

            ItemStack item = itemUsed.get();
            List<String> ids = this.resolveItemIds(item).stream().map(String::toLowerCase).toList();
            for (String value : values) {
                if (ids.contains(value.toLowerCase()))
                    return true;
            }
            return false;
        };
    }

    public Function<String, LootCondition> getInventoryContainsLootConditionFunction() {
        return tag -> new InventoryContainsCondition(tag, this::resolveItem, this.name().toLowerCase());
    }

    public static CustomItemPlugin fromString(String name) {
        for (CustomItemPlugin value : values())
            if (value.name().equalsIgnoreCase(name))
                return value;
        return null;
    }

}
