package dev.rosewood.roseloot.hook.items;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.Lazy;
import java.util.List;
import java.util.function.BiPredicate;
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
    NEXO(NexoItemProvider::new);

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

    public String resolveItemId(ItemStack itemStack) {
        return this.itemProvider.get().getItemId(itemStack);
    }

    public boolean supportsIdLookup() {
        return this.itemProvider.get().supportsIdLookup();
    }

    public String getConditionSuffix() {
        return this.itemProvider.get().getConditionSuffix();
    }

    public BiPredicate<LootContext, List<String>> getLootConditionPredicate() {
        return (context, values) -> context.getItemUsed()
                .map(this::resolveItemId)
                .filter(x -> values.stream().anyMatch(x::equalsIgnoreCase))
                .isPresent();
    }

    public static CustomItemPlugin fromString(String name) {
        for (CustomItemPlugin value : values())
            if (value.name().equalsIgnoreCase(name))
                return value;
        return null;
    }

}
