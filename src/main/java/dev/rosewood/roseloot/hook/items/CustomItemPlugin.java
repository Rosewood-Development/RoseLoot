package dev.rosewood.roseloot.hook.items;

import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import org.bukkit.inventory.ItemStack;

public enum CustomItemPlugin {

    ECO(EcoItemProvider::new),
    MMOITEMS(MMOItemProvider::new),
    MYTHICMOBS(MythicMobsItemProvider::new),
    ITEMBRIDGE(ItemBridgeItemProvider::new),
    EXECUTABLEITEMS(ExecutableItemProvider::new),
    EXECUTABLEBLOCKS(ExecutableBlockProvider::new),
    ITEMSADDER(ItemsAdderItemProvider::new),
    ITEMSXL(ItemsXLItemProvider::new),
    ORAXEN(OraxenItemProvider::new),
    KNOKKOCUSTOMITEMS(KnokkoCustomItemProvider::new),
    ITEMEDIT(ItemEditItemProvider::new),
    UBERITEMS(UberItemProvider::new),
    SLIMEFUN(SlimefunItemProvider::new),
    CUSTOMCRAFTING(CustomCraftingItemProvider::new);

    private final Supplier<ItemProvider> lazyLoader;
    private ItemProvider itemProvider;

    CustomItemPlugin(Supplier<ItemProvider> lazyLoader) {
        this.lazyLoader = lazyLoader;
    }

    public boolean isEnabled() {
        return this.load().isEnabled();
    }

    public ItemStack resolveItem(LootContext context, String id) {
        return this.load().getItem(context, id);
    }

    public String resolveItemId(ItemStack itemStack) {
        return this.load().getItemId(itemStack);
    }

    public boolean supportsIdLookup() {
        return this.load().supportsIdLookup();
    }

    public String getConditionSuffix() {
        return this.load().getConditionSuffix();
    }

    public BiPredicate<LootContext, List<String>> getLootConditionPredicate() {
        return (context, values) -> context.getItemUsed()
                .map(this::resolveItemId)
                .filter(x -> values.stream().anyMatch(x::equalsIgnoreCase))
                .isPresent();
    }

    private ItemProvider load() {
        if (this.itemProvider == null)
            this.itemProvider = this.lazyLoader.get();
        return this.itemProvider;
    }

    public static CustomItemPlugin fromString(String name) {
        for (CustomItemPlugin value : values())
            if (value.name().equalsIgnoreCase(name))
                return value;
        return null;
    }

}
